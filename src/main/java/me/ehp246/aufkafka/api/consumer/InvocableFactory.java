package me.ehp246.aufkafka.api.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Internal abstraction that creates an {@linkplain EventInvocable} given a
 * {@linkplain ConsumerRecord}.
 *
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface InvocableFactory {
    EventInvocable get(InboundEvent event);
}
