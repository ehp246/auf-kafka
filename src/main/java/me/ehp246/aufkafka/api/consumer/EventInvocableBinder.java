package me.ehp246.aufkafka.api.consumer;

import me.ehp246.aufkafka.core.consumer.DefaultEventInvocableBinder;

/**
 * The abstraction of binding an {@linkplain EventInvocable} to an {@linkplain InboundEvent} so it's ready to be invoked.
 * 
 * @author Lei Yang
 * @since 1.0
 * @see DefaultEventInvocableBinder
 */
@FunctionalInterface
public interface EventInvocableBinder {
    BoundInvocable bind(EventInvocable invocable, InboundEvent event);
}
