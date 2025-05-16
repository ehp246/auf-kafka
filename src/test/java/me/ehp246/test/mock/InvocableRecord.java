package me.ehp246.test.mock;

import java.lang.reflect.Method;

import me.ehp246.aufkafka.api.consumer.EventInvocable;
import me.ehp246.aufkafka.api.consumer.InvocationModel;

/**
 * @author Lei Yang
 *
 */
public record InvocableRecord(Object instance, Method method, AutoCloseable closeable,
        InvocationModel invocationModel) implements EventInvocable {
    public InvocableRecord {
        // Instance could be null for static invocation.
        if (method == null) {
            throw new IllegalArgumentException("Method must be specified");
        }

        if (invocationModel == null) {
            throw new IllegalArgumentException("Model must be specified");
        }
    }

    public InvocableRecord(final Object instance, final Method method) {
        this(instance, method, null, InvocationModel.DEFAULT);
    }

    @Override
    public void close() throws Exception {
    }
}
