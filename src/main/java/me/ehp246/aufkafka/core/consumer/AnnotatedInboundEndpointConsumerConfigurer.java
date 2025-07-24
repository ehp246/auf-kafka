package me.ehp246.aufkafka.core.consumer;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Value;

import me.ehp246.aufkafka.api.common.AufKafkaConstant;
import me.ehp246.aufkafka.api.consumer.InboundConsumerExecutorProvider;
import me.ehp246.aufkafka.api.consumer.InboundEndpoint;
import me.ehp246.aufkafka.api.consumer.InboundEndpointConsumerFactory;
import me.ehp246.aufkafka.api.spi.EventMdcContext;

/**
 * @author Lei Yang
 * @see AnnotatedInboundEndpointFactory
 */
public final class InboundEndpointConsumerConfigurer implements SmartInitializingSingleton {
    private final static Logger LOGGER = LoggerFactory.getLogger(InboundEndpointConsumerConfigurer.class);

    private final List<InboundEndpoint> endpoints;
    private final InboundEndpointConsumerFactory consumerFactory;
    private final InboundConsumerExecutorProvider executorProvider;
    private final String correlIdHeader;

    public InboundEndpointConsumerConfigurer(final List<InboundEndpoint> endpoints,
            final InboundEndpointConsumerFactory consumerFactory,
            final InboundConsumerExecutorProvider executorProvider,
            @Value("${" + AufKafkaConstant.PROPERTY_HEADER_CORRELATIONID + ":" + AufKafkaConstant.CORRELATIONID_HEADER
                    + "}") final String correlIdHeader) {
        super();
        this.endpoints = endpoints;
        this.consumerFactory = consumerFactory;
        this.executorProvider = executorProvider;
        this.correlIdHeader = correlIdHeader;
    }

    @Override
    public void afterSingletonsInstantiated() {
        for (final var endpoint : this.endpoints) {
            final var consumerRunner = this.consumerFactory.get(endpoint);

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
}
