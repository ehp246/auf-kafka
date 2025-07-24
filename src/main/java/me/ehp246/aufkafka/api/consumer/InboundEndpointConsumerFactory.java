package me.ehp246.aufkafka.api.consumer;

/**
 * @author Lei Yang
 */
@FunctionalInterface
public interface InboundEndpointConsumerFactory {
    InboundEndpointConsumer creat(InboundEndpoint endpoint);
}
