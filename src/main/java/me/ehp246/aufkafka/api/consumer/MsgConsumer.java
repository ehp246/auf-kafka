package me.ehp246.aufkafka.api.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface MsgConsumer {
    void apply(ConsumerRecord<String, String> msgRecord);
}
