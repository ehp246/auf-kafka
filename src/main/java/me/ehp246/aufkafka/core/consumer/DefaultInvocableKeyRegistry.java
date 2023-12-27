package me.ehp246.aufkafka.core.consumer;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import me.ehp246.aufkafka.api.consumer.Invocable;
import me.ehp246.aufkafka.api.consumer.InvocableKeyDefinition;
import me.ehp246.aufkafka.api.consumer.InvocableKeyRegistry;
import me.ehp246.aufkafka.api.consumer.InvocableType;
import me.ehp246.aufkafka.core.util.OneUtil;

/**
 *
 * {@linkplain ConsumerRecord#key()}-to-{@linkplain Invocable} registry.
 *
 * @author Lei Yang
 * @since 1.0
 */
final class DefaultInvocableKeyRegistry implements InvocableKeyRegistry {
    private final Map<String, InvocableKeyDefinition> cached = new ConcurrentHashMap<>();
    private final Map<String, InvocableKeyDefinition> registeredInvokables = new ConcurrentHashMap<>();
    private final Map<Class<?>, Map<String, Method>> registeredMethods = new ConcurrentHashMap<>();

    public DefaultInvocableKeyRegistry register(
            final Stream<InvocableKeyDefinition> invokingDefinitions) {
        invokingDefinitions.forEach(this::register);
        return this;
    }

    @Override
    public void register(final InvocableKeyDefinition invokingDefinition) {
        invokingDefinition.msgTypes().forEach(type -> {
            final var registered = registeredInvokables.putIfAbsent(type, invokingDefinition);
            if (registered != null) {
                throw new IllegalArgumentException(
                        "Duplicate type " + type + " from " + registered.type());
            }

            registeredMethods.put(invokingDefinition.type(), invokingDefinition.methods());
        });
    }

    @Override
    public Map<String, InvocableKeyDefinition> registered() {
        return Collections.unmodifiableMap(this.registeredInvokables);
    }

    @Override
    public InvocableType resolve(final ConsumerRecord<?, ?> msg) {
        final var msgKey = OneUtil.toString(Objects.requireNonNull(msg).key(), "");

        final var definition = this.cached.computeIfAbsent(msgKey,
                key -> registeredInvokables.entrySet().stream()
                        .filter(e -> msgKey.matches(e.getKey())).findAny().map(Map.Entry::getValue)
                        .orElse(null));

        if (definition == null) {
            return null;
        }

        final var method = registeredMethods.get(definition.type()).get("");

        if (method == null) {
            return null;
        }

        return new InvocableType(definition.type(), method, definition.scope(), definition.model());
    }
}
