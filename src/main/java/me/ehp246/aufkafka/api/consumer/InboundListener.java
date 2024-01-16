package me.ehp246.aufkafka.api.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Defines inbound message life-cycle events supported by
 * {@linkplain InboundEndpoint}.
 *
 * @author Lei Yang
 * @since 1.0
 */
public sealed interface InboundListener {
    @FunctionalInterface
    non-sealed interface DispatchingListener extends InboundListener {
        void onDispatching(ConsumerRecord<String, String> message);
    }

    @FunctionalInterface
    non-sealed interface UnmatchedListener extends InboundListener {
        void onUnmatched(ConsumerRecord<String, String> message);
    }

    @FunctionalInterface
    non-sealed interface ExceptionListener extends InboundListener {
        void onException(ExceptionListener.Context context);

        interface Context {
            Consumer<String, String> consumer();

            ConsumerRecord<String, String> message();

            java.lang.Exception thrown();
        }
    }
}