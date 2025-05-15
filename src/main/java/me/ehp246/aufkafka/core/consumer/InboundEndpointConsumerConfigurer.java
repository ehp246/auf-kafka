package me.ehp246.aufkafka.core.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import me.ehp246.aufkafka.api.consumer.InboundConsumerExecutorProvider;
import me.ehp246.aufkafka.api.consumer.InboundConsumerListener;
import me.ehp246.aufkafka.api.consumer.InboundDispatchingLogger;
import me.ehp246.aufkafka.api.consumer.InboundEndpoint;
import me.ehp246.aufkafka.api.consumer.EventInvocableBinder;

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
    private final List<InboundConsumerListener.DispatchingListener> onDispatching;
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;
    private final DefaultInboundConsumerRegistry consumerRegistry;

    public InboundEndpointConsumerConfigurer(final List<InboundEndpoint> endpoints,
            final InboundConsumerExecutorProvider executorProvider, final ConsumerProvider consumerProvider,
            final EventInvocableBinder binder, final InboundDispatchingLogger inboundDispatchingLogger,
            final AutowireCapableBeanFactory autowireCapableBeanFactory,
            final DefaultInboundConsumerRegistry consumerRegistry) {
        super();
        this.endpoints = endpoints;
        this.executorProvider = executorProvider;
        this.consumerProvider = consumerProvider;
        this.binder = binder;
        this.onDispatching = inboundDispatchingLogger == null ? List.of() : List.of(inboundDispatchingLogger);
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
        this.consumerRegistry = consumerRegistry;
    }

    @Override
    public void afterSingletonsInstantiated() {
        for (final var endpoint : this.endpoints) {
            LOGGER.atTrace().setMessage("Registering '{}' on '{}'").addArgument(endpoint::name)
                    .addArgument(() -> endpoint.from().topic()).log();

            final var consumer = this.consumerProvider.get(endpoint.consumerConfigName(),
                    endpoint.consumerProperties());
            consumer.subscribe(Set.of(endpoint.from().topic()));

            final var consumerRunner = new InboundConsumerRunner(consumer, new DefaultEventInvocableDispatcher(this.binder,
                    endpoint.invocationListener() == null ? null : List.of(endpoint.invocationListener()), null),
                    new AutowireCapableInvocableFactory(autowireCapableBeanFactory, endpoint.invocableRegistry()),
                    this.onDispatching, endpoint.unmatchedConsumer(), endpoint.consumerExceptionListener());

            this.consumerRegistry.put(endpoint.name(), consumerRunner);

            this.executorProvider.get().execute(consumerRunner);
        }
    }

    @Override
    public void close() throws Exception {
        LOGGER.atTrace().setMessage("Closing consumers").log();

        final var closedFuture = new ArrayList<CompletableFuture<Boolean>>(this.consumerRegistry.getNames().size());

        for (String name : this.consumerRegistry.getNames()) {
            if (!(this.consumerRegistry.get(name) instanceof InboundConsumerRunner runner)) {
                LOGGER.atWarn().setMessage("Unknown InboundConsumer of type: {}")
                        .addArgument(this.consumerRegistry.get(name).consumer()).log();
                continue;
            }

            closedFuture.add(runner.close());
        }

        closedFuture.stream().forEach(CompletableFuture::join);
    }
}
