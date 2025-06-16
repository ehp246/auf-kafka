package me.ehp246.aufkafka.core.producer;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.producer.OutboundEvent;
import me.ehp246.aufkafka.api.serializer.TypeOfJson;

/**
 * The abstraction that transforms an {@linkplain ByKafka} invocation into an
 * {@linkplain OutboundEvent}.
 * 
 * @author Lei Yang
 * @since 1.0
 */
interface ProxyInvocationBinder {
    OutboundEvent apply(Object target, Object[] args) throws Throwable;

    record HeaderParam(String name, Class<?> type) {
    }

    record ValueParam(int index, TypeOfJson typeOf) {
    }
}