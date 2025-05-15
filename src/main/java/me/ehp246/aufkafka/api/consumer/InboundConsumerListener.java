package me.ehp246.aufkafka.api.consumer;

/**
 * Defines inbound message life-cycle events supported by
 * {@linkplain InboundEndpoint}.
 *
 * @author Lei Yang
 * @since 1.0
 */
public sealed interface InboundConsumerListener {
    @FunctionalInterface
    non-sealed interface DispatchingListener extends InboundConsumerListener {
        void onDispatching(InboundEvent event);
    }
}