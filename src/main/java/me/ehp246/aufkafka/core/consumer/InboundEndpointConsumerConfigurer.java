package me.ehp246.aufkafka.core.consumer;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import me.ehp246.aufkafka.api.AufKafkaConstant;
import me.ehp246.aufkafka.api.consumer.InboundConsumerExecutorProvider;
import me.ehp246.aufkafka.api.consumer.InboundEndpoint;
import me.ehp246.aufkafka.api.consumer.InvocableBinder;
import me.ehp246.aufkafka.api.exception.UnknownKeyException;
import me.ehp246.aufkafka.api.spi.MsgMDCContext;

/**
 * @author Lei Yang
 *
 */
public final class InboundEndpointConsumerConfigurer implements SmartInitializingSingleton {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(InboundEndpointConsumerConfigurer.class);

    private final Set<InboundEndpoint> endpoints;
    private final InboundConsumerExecutorProvider executorProvider;
    private final InvocableBinder binder;
    private final ConsumerProvider consumerProvider;
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;

    public InboundEndpointConsumerConfigurer(final Set<InboundEndpoint> endpoints,
            final InboundConsumerExecutorProvider executorProvider,
            final ConsumerProvider consumerProvider, final InvocableBinder binder,
            final AutowireCapableBeanFactory autowireCapableBeanFactory) {
        super();
        this.endpoints = endpoints;
        this.executorProvider = executorProvider;
        this.consumerProvider = consumerProvider;
        this.binder = binder;
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
    }

    public void afterSingletonsInstantiated() {
        for (final var endpoint : this.endpoints) {
            LOGGER.atTrace().setMessage("Registering '{}' on '{}'").addArgument(endpoint::name)
                    .addArgument(() -> endpoint.from().topic()).log();

            final var executor = this.executorProvider.get();
            final var consumer = this.consumerProvider.get(endpoint.consumerConfigName());
            final var dispatcher = new DefaultInvocableDispatcher(this.binder,
                    endpoint.invocationListener() == null ? null
                            : List.of(endpoint.invocationListener()),
                    null);
            final var invocableFactory = new AutowireCapableInvocableFactory(
                    autowireCapableBeanFactory, endpoint.keyRegistry());
            final var defaultConsumer = endpoint.defaultConsumer();

            executor.execute(() -> {
                consumer.subscribe(Set.of(endpoint.from().topic()));

                while (true) {
                    final var polled = consumer.poll(Duration.ofMillis(100));
                    LOGGER.atTrace().setMessage("Polled count: {}").addArgument(polled::count)
                            .log();

                    for (final var msg : polled) {
                        LOGGER.atTrace().setMessage("Received {}").addArgument(msg::key).log();

                        try (final var closeble = MsgMDCContext.set(msg);) {
                            LOGGER.atDebug().addMarker(AufKafkaConstant.HEADERS)
                                    .setMessage("{}, {}").addArgument(msg::topic)
                                    .addArgument(msg::key).log();
                            LOGGER.atTrace().addMarker(AufKafkaConstant.VALUE).setMessage("{}")
                                    .addArgument(msg::value).log();

                            final var invocable = invocableFactory.get(msg);

                            if (invocable == null) {
                                if (defaultConsumer == null) {
                                    throw new UnknownKeyException(msg);
                                } else {
                                    defaultConsumer.apply(msg);
                                    return;
                                }
                            }

                            dispatcher.dispatch(invocable, msg);

                            consumer.commitSync();
                        } catch (Exception e) {
                            LOGGER.atError().addMarker(AufKafkaConstant.EXCEPTION).setCause(e)
                                    .setMessage("Ignored: {}").addArgument(e).log();
                        }
                    }
                }
            });
        }
    }
}
