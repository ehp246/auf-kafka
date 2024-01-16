package me.ehp246.aufkafka.api.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Defines life-cycle events supported by {@linkplain InboundEndpoint}.
 *
 * @author Lei Yang
 * @since 1.0
 */
public sealed interface ConsumerListener {
    @FunctionalInterface
    non-sealed interface ReceivedListener extends ConsumerListener {
        void onReceived(ConsumerRecord<String, String> msg);
    }

    @FunctionalInterface
    non-sealed interface UnmatchedListener extends ConsumerListener {
        void onUnmatched(ConsumerRecord<String, String> msg);
    }

    @FunctionalInterface
    non-sealed interface ExceptionListener extends ConsumerListener {
        void onException(ExceptionListener.ExceptionContext context);

        interface ExceptionContext {
            Consumer<String, String> consumer();

            ConsumerRecord<String, String> received();

            java.lang.Exception thrown();
        }
    }
}