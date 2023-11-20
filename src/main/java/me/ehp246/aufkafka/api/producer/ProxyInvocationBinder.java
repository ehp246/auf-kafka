package me.ehp246.aufkafka.api.producer;

/**
 * @author Lei Yang
 * @since 1.0
 */
public interface ProxyInvocationBinder {
    Bound apply(Object target, Object[] args) throws Throwable;

    record Bound(OutboundRecord message) {
    }
}