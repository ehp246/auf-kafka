package me.ehp246.aufkafka.core.consumer;

import org.apache.kafka.clients.consumer.Consumer;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ConsumerProvider {
    Consumer<String, String> get(String configName);
}
