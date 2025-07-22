package me.ehp246.aufkafka.core.consumer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import me.ehp246.aufkafka.api.common.AufKafkaConstant;
import me.ehp246.aufkafka.api.consumer.DispatchListener;
import me.ehp246.aufkafka.api.consumer.EventInvocableBinder;
import me.ehp246.aufkafka.api.consumer.InboundConsumerExecutorProvider;
import me.ehp246.aufkafka.api.consumer.InboundDispatchingLogger;
import me.ehp246.aufkafka.api.consumer.InboundEndpoint;
import me.ehp246.aufkafka.api.spi.EventMdcContext;

/**
 * @author Lei Yang
 * @see InboundEndpointFactory
 */
public final class InboundEndpointConsumerConfigurer implements SmartInitializingSingleton, AutoCloseable {
    private final static Logger LOGGER = LoggerFactory.getLogger(InboundEndpointConsumerConfigurer.class);

    private final List<InboundEndpoint> endpoints;
    private final InboundConsumerExecutorProvider executorProvider;
    private final EventInvocableBinder binder;
    private final ConsumerProvider consumerProvider;
    private final List<DispatchListener.DispatchingListener> onDispatching;
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;
    private final DefaultInboundConsumerRegistry consumerRegistry;
    private final String correlIdHeader;

    public InboundEndpointConsumerConfigurer(final List<InboundEndpoint> endpoints,
            final InboundConsumerExecutorProvider executorProvider, final ConsumerProvider consumerProvider,
            final EventInvocableBinder binder, final InboundDispatchingLogger inboundDispatchingLogger,
            final AutowireCapableBeanFactory autowireCapableBeanFactory,
            final DefaultInboundConsumerRegistry consumerRegistry,
            @Value("${" + AufKafkaConstant.PROPERTY_HEADER_CORRELATIONID + ":" + AufKafkaConstant.CORRELATIONID_HEADER
                    + "}") final String correlIdHeader) {
        super();
        this.endpoints = endpoints;
        this.executorProvider = executorProvider;
        this.consumerProvider = consumerProvider;
        this.binder = binder;
        this.onDispatching = inboundDispatchingLogger == null ? List.of() : List.of(inboundDispatchingLogger);
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
        this.consumerRegistry = consumerRegistry;
        this.correlIdHeader = correlIdHeader;
    }

    @Override
    public void afterSingletonsInstantiated() {
        for (final var endpoint : this.endpoints) {
            final var topic = endpoint.from().topic();
            final var partitions = endpoint.from().partitions();

            LOGGER.atTrace().setMessage("Registering '{}' on '{}:{}'").addArgument(endpoint::name).addArgument(topic)
                    .addArgument(partitions).log();

            final var consumer = this.consumerProvider.get(endpoint.configName(), endpoint.consumerProperties());

            if (partitions == null || partitions.size() == 0) {
                consumer.subscribe(Set.of(topic));
            } else {
                consumer.assign(partitions.stream().map(i -> new TopicPartition(topic, i)).toList());
            }

            final var consumerRunner = new DefaultInboundEndpointConsumer(consumer, endpoint::pollDuration,
                    new DefaultEventInvocableRunnableBuilder(this.binder,
                            endpoint.invocationListener() == null ? null : List.of(endpoint.invocationListener())),
                    new AutowireCapableInvocableFactory(autowireCapableBeanFactory, endpoint.invocableRegistry()),
                    this.onDispatching, endpoint.unknownEventListener(), endpoint.dispatchExceptionListener());

            this.consumerRegistry.put(endpoint.name(), consumerRunner);

            this.executorProvider.get().execute(() -> {
                try (final var closeable = EventMdcContext.setMdcHeaders(Set.of(this.correlIdHeader))) {
                    consumerRunner.run();
                } catch (Exception e) {
                    LOGGER.atError().setCause(e).setMessage("{} consumer failed. Terminating.")
                            .addArgument(endpoint.name()).log();
                }
            });
        }
    }

    @Override
    public void close() throws Exception {
        LOGGER.atTrace().setMessage("Closing consumers").log();

        final var closedFutures = new HashSet<CompletableFuture<Void>>(this.consumerRegistry.getNames().size());

        this.consumerRegistry.getNames().forEach(name -> closedFutures.add(this.consumerRegistry.get(name).close()));

        closedFutures.stream().forEach(CompletableFuture::join);
    }
}
