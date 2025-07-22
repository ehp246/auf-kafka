package me.ehp246.aufkafka.api.consumer;

import me.ehp246.aufkafka.api.annotation.EnableForKafka;

/**
 * Defines inbound message life-cycle events supported by
 * {@linkplain InboundEndpoint}.
 *
 * @author Lei Yang
 * @since 1.0
 */
public sealed interface DispatchListener {
    @FunctionalInterface
    non-sealed interface DispatchingListener extends DispatchListener {
        void onDispatching(InboundEventContext eventContext);
    }

    /**
     * Consumes the {@linkplain InboundEvent} for which an
     * {@linkplain EventInvocable} can't be found.
     * 
     * @author Lei Yang
     * @see EnableForKafka.Inbound#unknownEventListener()
     */
    @FunctionalInterface
    non-sealed interface UnknownEventListener extends DispatchListener {
        void onUnknown(InboundEventContext eventContext);
    }

    /**
     * Exception from the listener will be logged and ignored.
     */
    @FunctionalInterface
    non-sealed interface ExceptionListener extends DispatchListener {
        void onException(InboundEventContext eventContext, Exception thrown);
    }
}