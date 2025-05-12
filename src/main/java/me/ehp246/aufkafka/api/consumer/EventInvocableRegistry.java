package me.ehp246.aufkafka.api.consumer;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * The abstraction of a {@linkplain InvocableType} registry for an
 * {@linkplain InboundEndpoint}.
 * <p>
 * Updates on the registry should take effect immediately.
 * <p>
 * Must be thread safe.
 *
 * @author Lei Yang
 * @since 1.0
 * @see InboundEndpoint
 * @see EventInvocableNameSource
 */
public interface EventInvocableRegistry {
    /**
     * Register a new definition.
     */
    void register(EventInvocableNameSource nameType, EventInvocableDefinition definition);

    /**
     * Returns an un-modifiable copy of all registered.
     */
    Map<String, EventInvocableDefinition> registered(EventInvocableNameSource keyType);

    /**
     * Resolves a {@linkplain ConsumerRecord} to an {@linkplain InvocableType}.
     *
     * @return <code>null</code> if no match found.
     */
    InvocableType resolve(ConsumerRecord<?, ?> event);
}
