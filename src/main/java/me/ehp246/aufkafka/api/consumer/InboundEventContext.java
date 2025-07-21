package me.ehp246.aufkafka.api.consumer;

import org.apache.kafka.clients.consumer.Consumer;

/**
 * @author Lei Yang
 */
public record InboundEventContext(InboundEvent event, Consumer<String, String> consumer) {
}
