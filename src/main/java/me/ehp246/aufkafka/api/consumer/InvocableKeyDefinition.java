package me.ehp246.aufkafka.api.consumer;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * The definition of a Java type that is invokable by
 * {@linkplain ConsumerRecord#key()} and to be registered in a
 * {@linkplain InvocableKeyRegistry}.
 *
 * @author Lei Yang
 * @since 1.0
 */
public record InvocableKeyDefinition(Set<String> msgTypes, Class<?> type,
        Map<String, Method> methods, InstanceScope scope, InvocationModel model) {

    public InvocableKeyDefinition(final Set<String> msgKeys, final Class<?> type,
            final Map<String, Method> methods) {
        this(msgKeys, type, methods, InstanceScope.MESSAGE, InvocationModel.DEFAULT);
    }
}
