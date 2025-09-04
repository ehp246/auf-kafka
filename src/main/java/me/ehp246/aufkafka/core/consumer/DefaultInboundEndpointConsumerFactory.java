package me.ehp246.aufkafka.core.consumer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import me.ehp246.aufkafka.api.common.AufKafkaConstant;
import me.ehp246.aufkafka.api.consumer.DispatchListener;
import me.ehp246.aufkafka.api.consumer.EventInvocableBinder;
import me.ehp246.aufkafka.api.consumer.InboundConsumerExecutorProvider;
import me.ehp246.aufkafka.api.consumer.InboundEndpoint;
import me.ehp246.aufkafka.api.consumer.InboundEndpointConsumer;
import me.ehp246.aufkafka.api.consumer.InboundEndpointConsumerFactory;
import me.ehp246.aufkafka.api.spi.EventMdcContext;

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
    private final InboundConsumerExecutorProvider executorProvider;
    private final String correlIdHeader;

    public DefaultInboundEndpointConsumerFactory(final ConsumerProvider consumerProvider,
            List<DispatchListener.DispatchingListener> onDispatching,
            AutowireCapableBeanFactory autowireCapableBeanFactory, EventInvocableBinder binder,
            DefaultInboundEndpointConsumerRegistry consumerRegistry,
            final InboundConsumerExecutorProvider executorProvider,
            @Value("${" + AufKafkaConstant.PROPERTY_HEADER_CORRELATIONID + ":" + AufKafkaConstant.CORRELATIONID_HEADER
                    + "}") final String correlIdHeader) {
        super();
        this.consumerProvider = consumerProvider;
        this.binder = binder;
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
        this.onDispatching = onDispatching;
        this.consumerRegistry = consumerRegistry;
        this.executorProvider = executorProvider;
        this.correlIdHeader = correlIdHeader;
    }

    @Override
    public InboundEndpointConsumer creat(final InboundEndpoint endpoint) {
        final var consumer = this.consumerProvider.get(endpoint.configName(), endpoint.consumerProperties());

        final var endpointConsumer = new DefaultInboundEndpointConsumer(endpoint.at(), consumer, endpoint::pollDuration,
                new DefaultEventInvocableRunnableBuilder(this.binder,
                        endpoint.invocationListener() == null ? null : List.of(endpoint.invocationListener())),
                new AutowireCapableInvocableFactory(autowireCapableBeanFactory, endpoint.invocableRegistry()),
                this.onDispatching, endpoint.unknownEventListener(), endpoint.dispatchExceptionListener());

        this.consumerRegistry.put(endpoint.name(), endpointConsumer);

        this.executorProvider.get().execute(() -> {
            try (final var closeable = EventMdcContext.setMdcHeaders(Set.of(this.correlIdHeader))) {
                endpointConsumer.run();
            } catch (Exception e) {
                LOGGER.atError().setCause(e).setMessage("Endpoint:{} consumer failed to start.")
                        .addArgument(endpoint.name()).log();
            }
        });

        return endpointConsumer;
    }

    @Override
    public void close() throws Exception {
        LOGGER.atTrace().setMessage("Closing endpoint consumers").log();

        final var closedFutures = HashSet.<CompletableFuture<Void>>newHashSet(this.consumerRegistry.getNames().size());

        this.consumerRegistry.getNames().forEach(name -> closedFutures.add(this.consumerRegistry.get(name).close()));

        closedFutures.stream().forEach(CompletableFuture::join);
    }
}
