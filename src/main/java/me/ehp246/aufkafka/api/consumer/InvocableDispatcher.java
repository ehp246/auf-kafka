package me.ehp246.aufkafka.api.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface InvocableDispatcher {
    void dispatch(final Invocable invocable, final ConsumerRecord<String, String> msg);
}
