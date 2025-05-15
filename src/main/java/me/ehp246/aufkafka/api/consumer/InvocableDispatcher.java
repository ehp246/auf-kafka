package me.ehp246.aufkafka.api.consumer;

/**
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface InvocableDispatcher {
    void dispatch(Invocable invocable, InboundEvent event);
}
