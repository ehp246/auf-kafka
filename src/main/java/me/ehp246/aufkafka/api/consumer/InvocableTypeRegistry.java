package me.ehp246.aufkafka.api.consumer;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import me.ehp246.aufkafka.api.annotation.ForKey;

/**
 * The abstraction of a {@linkplain ForKey} registry for an
 * {@linkplain InboundEndpoint}.
 * <p>
 * Updates on the registry should take effect immediately.
 * <p>
 * Must be thread safe.
 *
 * @author Lei Yang
 * @since 1.0
 * @see InboundEndpoint
 */
public interface InvocableTypeRegistry {
    /**
     * Register a new definition.
     */
    void register(InvocableTypeDefinition definition);

    /**
     * Returns an un-modifiable copy of all registered.
     */
    Map<String, InvocableTypeDefinition> registered();

    /**
     * Resolves a {@linkplain ConsumerRecord} to an {@linkplain InvocableType}.
     *
     * @return <code>null</code> if no match found.
     */
    InvocableType resolve(ConsumerRecord<?, ?> msg);
}
