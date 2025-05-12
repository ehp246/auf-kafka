package me.ehp246.aufkafka.api.consumer;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import me.ehp246.aufkafka.api.annotation.ForEvent;
import me.ehp246.aufkafka.api.annotation.ForKey;

/**
 * Defines a type that is to be registered in a
 * {@linkplain EventInvocableRegistry} and is invokable by a
 * {@linkplain ConsumerRecord}.
 *
 * @author Lei Yang
 * @since 1.0
 * @see ForKey
 * @see ForEvent
 */
public record EventInvocableDefinition(Set<String> names, Class<?> type, Map<String, Method> methods,
        InstanceScope scope, InvocationModel model) {

    public EventInvocableDefinition(final Set<String> eventKeys, final Class<?> type,
            final Map<String, Method> methods) {
        this(eventKeys, type, methods, InstanceScope.EVENT, InvocationModel.DEFAULT);
    }
}
