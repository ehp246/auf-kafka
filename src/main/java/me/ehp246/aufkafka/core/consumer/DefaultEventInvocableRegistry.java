package me.ehp246.aufkafka.core.consumer;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import me.ehp246.aufkafka.api.AufKafkaConstant;
import me.ehp246.aufkafka.api.consumer.EventInvocableDefinition;
import me.ehp246.aufkafka.api.consumer.EventInvocableKeyType;
import me.ehp246.aufkafka.api.consumer.EventInvocableRegistry;
import me.ehp246.aufkafka.api.consumer.InvocableType;
import me.ehp246.aufkafka.core.util.OneUtil;

/**
 * The default implementation of a {@linkplain EventInvocableRegistry}.
 * <p>
 * This implementation matches a {@linkplain ConsumerRecord} to a
 * {@linkplain InvocalType} in the following order:
 * <ul>
 * <li>{@linkplain EventInvocableKeyType#EVENT_HEADER}.</li>
 * <li>{@linkplain ConsumerRecord#key()}.</li>
 * </ul>
 * <p>
 * Must be thread safe.
 * 
 * @author Lei Yang
 * @since 1.0
 */
final class DefaultEventInvocableRegistry implements EventInvocableRegistry {
    private final String eventTypeHeader;
    private final Map<EventInvocableKeyType, Map<String, EventInvocableDefinition>> cached = new ConcurrentHashMap<>();
    private final Map<EventInvocableKeyType, Map<String, EventInvocableDefinition>> registeredInvokables = new ConcurrentHashMap<>();
    /**
     * A shortcut lookup map from a matched type to the invoke method.
     */
    private final Map<Class<?>, Map<String, Method>> registeredMethods = new ConcurrentHashMap<>();

    DefaultEventInvocableRegistry(final String eventTypeHeader) {
        this.eventTypeHeader = Objects.requireNonNull(eventTypeHeader);
        for (final var type : EventInvocableKeyType.values()) {
            this.registeredInvokables.put(type, new ConcurrentHashMap<>());
            this.cached.put(type, new ConcurrentHashMap<>());
        }
    }

    @Override
    public void register(final EventInvocableKeyType keyType, final EventInvocableDefinition invokingDefinition) {
        final var invokables = this.registeredInvokables.get(keyType);

        invokingDefinition.names().forEach(type -> {
            final var registered = invokables.putIfAbsent(type, invokingDefinition);
            if (registered != null) {
                throw new IllegalArgumentException("Duplicate type " + type + " from " + registered.type());
            }

            /*
             * For now, one applying method for each invokable type. No named method yet.
             */
            registeredMethods.put(invokingDefinition.type(), invokingDefinition.methods());
        });
    }

    @Override
    public Map<String, EventInvocableDefinition> registered(final EventInvocableKeyType keyType) {
        return Collections.unmodifiableMap(this.registeredInvokables.get(keyType));
    }

    /**
     * If the incoming event has
     * {@linkplain AufKafkaConstant#EVENT_HEADER} defined, the value will
     * be used as the key for the event-type registry. Otherwise,
     * {@linkplain ConsumerRecord#key()} will be used as the key to look up the key
     * registry.
     * <p>
     * No cross reference between the two registries.
     * 
     */
    @Override
    public InvocableType resolve(final ConsumerRecord<?, ?> event) {
        Objects.requireNonNull(event);
        /**
         * Look up by event type header first.
         */
        final var eventType = OneUtil.getLastHeaderAsString(event, this.eventTypeHeader);

        final var lookupkey = eventType != null ? eventType : OneUtil.toString(event.key(), "");
        final var keyType = eventType != null ? EventInvocableKeyType.EVENT_HEADER : EventInvocableKeyType.KEY;

        final var definition = this.cached.get(keyType).computeIfAbsent(lookupkey,
                key -> this.registeredInvokables.get(keyType).entrySet().stream()
                        .filter(entry -> lookupkey.matches(entry.getKey())).findAny().map(Map.Entry::getValue)
                        .orElse(null));

        if (definition == null) {
            return null;
        }

        return new InvocableType(definition.type(), registeredMethods.get(definition.type()).get(""),
                definition.scope(), definition.model());
    }
}
