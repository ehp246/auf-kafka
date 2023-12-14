package me.ehp246.aufkafka.api.consumer;

import org.apache.kafka.clients.consumer.Consumer;

/**
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface ConsumerProvider {
    Consumer<String, String> get(String name);
}
