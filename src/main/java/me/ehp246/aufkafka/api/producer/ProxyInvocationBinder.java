package me.ehp246.aufkafka.api.producer;

import me.ehp246.aufkafka.api.serializer.ObjectOf;

/**
 * @author Lei Yang
 * @since 1.0
 */
public interface ProxyInvocationBinder {
    Bound apply(Object target, Object[] args) throws Throwable;

    record Bound(OutboundEvent message) {
    }

    record HeaderParam(String name, Class<?> type) {
    }

    record ValueParam(int index, ObjectOf<?> objectOf) {
    }
}