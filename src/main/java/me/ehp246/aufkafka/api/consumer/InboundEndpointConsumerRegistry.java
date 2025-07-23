package me.ehp246.aufkafka.api.consumer;

/**
 * @author Lei Yang
 */
public interface InboundEndpointConsumerRegistry {
	InboundEndpointConsumer get(String name);
}
