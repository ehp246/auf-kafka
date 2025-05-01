package me.ehp246.aufkafka.api.consumer;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * The definition of a Java type that is invokable by a
 * {@linkplain ConsumerRecord} and to be registered in a
 * {@linkplain EventInvocableRegistry}.
 *
 * @author Lei Yang
 * @since 1.0
 */
public record EventInvocableDefinition(Set<String> lookupKeys, Class<?> type, Map<String, Method> methods,
        InstanceScope scope, InvocationModel model) {

    public EventInvocableDefinition(final Set<String> eventKeys, final Class<?> type,
            final Map<String, Method> methods) {
        this(eventKeys, type, methods, InstanceScope.MESSAGE, InvocationModel.DEFAULT);
    }
}
