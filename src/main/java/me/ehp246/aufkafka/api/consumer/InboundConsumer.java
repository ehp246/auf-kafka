package me.ehp246.aufkafka.api.consumer;

import org.apache.kafka.clients.consumer.Consumer;

/**
 * @author Lei Yang
 *
 */
public interface InboundConsumer<K, V> {
    InboundEndpoint inboundEndpoint();

    Consumer<K, V> consumer();
}
