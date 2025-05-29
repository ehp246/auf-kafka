package me.ehp246.aufkafka.api.producer;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.serializer.ObjectOf;

/**
 * The abstraction that transforms an {@linkplain ByKafka} invocation into an
 * {@linkplain OutboundEvent}.
 * 
 * @author Lei Yang
 * @since 1.0
 */
public interface ProxyInvocationBinder {
    OutboundEvent apply(Object target, Object[] args) throws Throwable;

    record HeaderParam(String name, Class<?> type) {
    }

    record ValueParam(int index, ObjectOf<?> objectOf) {
    }
}