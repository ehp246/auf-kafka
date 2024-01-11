package me.ehp246.aufkafka.core.consumer;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import me.ehp246.aufkafka.api.consumer.ConsumerListener;
import me.ehp246.aufkafka.api.consumer.InboundConsumerExecutorProvider;
import me.ehp246.aufkafka.api.consumer.InboundEndpoint;
import me.ehp246.aufkafka.api.consumer.InvocableBinder;

/**
 * @author Lei Yang
 * @see InboundEndpointFactory
 */
public final class InboundEndpointConsumerConfigurer implements SmartInitializingSingleton {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(InboundEndpointConsumerConfigurer.class);

    private final List<InboundEndpoint> endpoints;
    private final InboundConsumerExecutorProvider executorProvider;
    private final InvocableBinder binder;
    private final ConsumerProvider consumerProvider;
    private final List<ConsumerListener> inboudEventListeners;
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;

    public InboundEndpointConsumerConfigurer(final List<InboundEndpoint> endpoints,
            final InboundConsumerExecutorProvider executorProvider,
            final ConsumerProvider consumerProvider, final InvocableBinder binder,
            final List<ConsumerListener> eventListeners,
            final AutowireCapableBeanFactory autowireCapableBeanFactory) {
        super();
        this.endpoints = endpoints;
        this.executorProvider = executorProvider;
        this.consumerProvider = consumerProvider;
        this.binder = binder;
        this.inboudEventListeners = eventListeners;
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
    }

    public void afterSingletonsInstantiated() {
        for (final var endpoint : this.endpoints) {
            LOGGER.atTrace().setMessage("Registering '{}' on '{}'").addArgument(endpoint::name)
                    .addArgument(() -> endpoint.from().topic()).log();

            final var consumer = this.consumerProvider.get(endpoint.consumerConfigName());
            consumer.subscribe(Set.of(endpoint.from().topic()));

            final var consumerTask = new ConsumerTask(consumer,
                    new DefaultInvocableDispatcher(this.binder,
                            endpoint.invocationListener() == null ? null
                                    : List.of(endpoint.invocationListener()),
                            null),
                    new AutowireCapableInvocableFactory(autowireCapableBeanFactory,
                            endpoint.keyRegistry()),
                    this.inboudEventListeners);

            this.executorProvider.get().execute(consumerTask);
        }
    }
}
