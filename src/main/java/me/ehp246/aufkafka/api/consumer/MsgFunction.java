package me.ehp246.aufkafka.api.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * @author Lei Yang
 * @sine 1.0
 */
@FunctionalInterface
public interface MsgFunction {
    void apply(ConsumerRecord<String, String> msgRecord);
}
