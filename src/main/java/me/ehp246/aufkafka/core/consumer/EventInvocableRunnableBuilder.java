package me.ehp246.aufkafka.core.consumer;

import java.util.concurrent.Executor;

import me.ehp246.aufkafka.api.consumer.EventInvocable;
import me.ehp246.aufkafka.api.consumer.InboundEvent;
import me.ehp246.aufkafka.api.consumer.InboundEventContext;
import me.ehp246.aufkafka.api.consumer.InvocationListener;
import me.ehp246.aufkafka.api.consumer.InvocationModel;

/**
 * The abstraction that builds a {@linkplain Runnable} from an
 * {@linkplain EventInvocable} and an {@linkplain InboundEvent}. The
 * {@linkplain Runnable} should contain all the logic that is outside of
 * {@linkplain EventInvocable}, e.g., {@linkplain InvocationListener}'s,
 * logging, exception processing, and replying. The {@linkplain Runnable} will
 * be submitted to an {@linkplain Executor} for execution as is with no
 * additional processing.
 * <p>
 * {@linkplain InvocationModel} is outside of the scope for the builder.
 * 
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
interface EventInvocableRunnableBuilder {
    Runnable apply(EventInvocable eventInvocable, InboundEventContext event);
}
