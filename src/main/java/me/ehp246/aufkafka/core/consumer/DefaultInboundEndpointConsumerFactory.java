package me.ehp246.aufkafka.core.consumer;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import me.ehp246.aufkafka.api.consumer.DispatchListener;
import me.ehp246.aufkafka.api.consumer.EventInvocableBinder;
import me.ehp246.aufkafka.api.consumer.InboundEndpoint;
import me.ehp246.aufkafka.api.consumer.InboundEndpointConsumer;
import me.ehp246.aufkafka.api.consumer.InboundEndpointConsumerFactory;

/**
 * @author Lei Yang
 */
public final class DefaultInboundEndpointConsumerFactory implements InboundEndpointConsumerFactory, AutoCloseable {
    private final Logger LOGGER = LoggerFactory.getLogger(DefaultInboundEndpointConsumerFactory.class);

    private final ConsumerProvider consumerProvider;
    private final EventInvocableBinder binder;
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;
    private final List<DispatchListener.DispatchingListener> onDispatching;
    private final DefaultInboundEndpointConsumerRegistry consumerRegistry;

    public DefaultInboundEndpointConsumerFactory(final ConsumerProvider consumerProvider,
            List<DispatchListener.DispatchingListener> onDispatching,
            AutowireCapableBeanFactory autowireCapableBeanFactory, EventInvocableBinder binder,
            DefaultInboundEndpointConsumerRegistry consumerRegistry) {
        super();
        this.consumerProvider = consumerProvider;
        this.binder = binder;
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
        this.onDispatching = onDispatching;
        this.consumerRegistry = consumerRegistry;
    }

    @Override
    public InboundEndpointConsumer get(final InboundEndpoint endpoint) {
        final var consumer = this.consumerProvider.get(endpoint.configName(), endpoint.consumerProperties());

        final var endpointConsumer = new DefaultInboundEndpointConsumer(endpoint.from(), consumer,
                endpoint::pollDuration,
                new DefaultEventInvocableRunnableBuilder(this.binder,
                        endpoint.invocationListener() == null ? null : List.of(endpoint.invocationListener())),
                new AutowireCapableInvocableFactory(autowireCapableBeanFactory, endpoint.invocableRegistry()),
                this.onDispatching, endpoint.unknownEventListener(), endpoint.dispatchExceptionListener());

        this.consumerRegistry.put(endpoint.name(), endpointConsumer);

        return endpointConsumer;
    }

    @Override
    public void close() throws Exception {
        LOGGER.atTrace().setMessage("Closing consumers").log();

        final var closedFutures = new HashSet<CompletableFuture<Void>>(this.consumerRegistry.getNames().size());

        this.consumerRegistry.getNames().forEach(name -> closedFutures.add(this.consumerRegistry.get(name).close()));

        closedFutures.stream().forEach(CompletableFuture::join);
    }
}
