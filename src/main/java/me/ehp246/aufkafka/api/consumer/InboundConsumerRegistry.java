package me.ehp246.aufkafka.api.consumer;

/**
 * @author Lei Yang
 */
public interface InboundConsumerRegistry {
	InboundEndpointConsumer get(String name);
}
