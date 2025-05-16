package me.ehp246.aufkafka.api.consumer;

/**
 * 
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface EventInvocableBinder {
    BoundInvocable bind(EventInvocable eventInvocable, InboundEvent event);
}
