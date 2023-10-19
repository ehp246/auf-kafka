package me.ehp246.aufkafka.core.producer;

import java.lang.reflect.Proxy;

/**
 * The abstraction that turns an invocation on a {@linkplain Proxy} into a
 * {@linkplain RestRequest}.
 * <p>
 * Produced by a {@linkplain ProxyMethodParser}.
 *
 * @author Lei Yang
 */
public interface ProxyInvocationBinder {
    Bound apply(Object target, Object[] args) throws Throwable;

    record Bound() {
    }
}