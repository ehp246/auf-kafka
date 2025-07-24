package me.ehp246.aufkafka.core.consumer;

import java.util.List;

import org.springframework.beans.factory.SmartInitializingSingleton;

import me.ehp246.aufkafka.api.consumer.InboundEndpoint;
import me.ehp246.aufkafka.api.consumer.InboundEndpointConsumerFactory;

/**
 * @author Lei Yang
 * @see AnnotatedInboundEndpointFactory
 */
public final class AnnotatedInboundEndpointConsumerConfigurer implements SmartInitializingSingleton {

    private final List<InboundEndpoint> endpoints;
    private final InboundEndpointConsumerFactory consumerFactory;

    public AnnotatedInboundEndpointConsumerConfigurer(final List<InboundEndpoint> endpoints,
            final InboundEndpointConsumerFactory consumerFactory) {
        super();
        this.endpoints = endpoints;
        this.consumerFactory = consumerFactory;
    }

    @Override
    public void afterSingletonsInstantiated() {
        this.endpoints.stream().forEach(this.consumerFactory::creat);
    }
}
