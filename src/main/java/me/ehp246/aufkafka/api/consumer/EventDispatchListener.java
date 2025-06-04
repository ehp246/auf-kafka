package me.ehp246.aufkafka.api.consumer;

import org.apache.kafka.clients.consumer.Consumer;

/**
 * Defines inbound message life-cycle events supported by
 * {@linkplain InboundEndpoint}.
 *
 * @author Lei Yang
 * @since 1.0
 */
public sealed interface EventDispatchListener {
    @FunctionalInterface
    non-sealed interface DispatchingListener extends EventDispatchListener {
	void onDispatching(InboundEvent event);
    }

    @FunctionalInterface
    non-sealed interface ExceptionListener extends EventDispatchListener {
	void onException(ExceptionListener.Context context);

	record Context(Consumer<String, String> consumer, InboundEvent event, Exception thrown) {
	}
    }
}