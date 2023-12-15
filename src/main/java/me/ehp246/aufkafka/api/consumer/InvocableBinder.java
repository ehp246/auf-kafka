package me.ehp246.aufkafka.api.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * 
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface InvocableBinder {
    BoundInvocable bind(Invocable invocable, ConsumerRecord<?, ?> msg);
}
