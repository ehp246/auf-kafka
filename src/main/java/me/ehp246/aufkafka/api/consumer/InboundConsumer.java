package me.ehp246.aufkafka.api.consumer;

import org.apache.kafka.clients.consumer.Consumer;

/**
 * @author Lei Yang
 * @since 1.0
 */
public interface InboundConsumer {
    InboundEndpoint inboundEndpoint();

    Consumer<String, String> consumer();
}
