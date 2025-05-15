package me.ehp246.aufkafka.api.consumer;

/**
 * 
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface InvocableBinder {
    BoundInvocable bind(Invocable invocable, InboundEvent event);
}
