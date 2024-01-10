package me.ehp246.aufkafka.api.consumer;

import java.util.function.Consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface ConsumerFn extends Consumer<ConsumerRecord<String, String>> {
}
