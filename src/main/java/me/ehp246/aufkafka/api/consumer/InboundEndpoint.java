package me.ehp246.aufkafka.api.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * @author Lei Yang
 * @since 1.0
 */
public interface InboundEndpoint {
    From from();

    InvocableKeyRegistry keyRegistry();

    default String name() {
        return null;
    }

    default String consumerConfigName() {
        return null;
    }

    default boolean autoStartup() {
        return true;
    }

    default InvocationListener invocationListener() {
        return null;
    }

    default EventListener.UnmatchedListener defaultConsumer() {
        return null;
    }

    default EventListener.ExceptionListener consumerExceptionListener() {
        return null;
    }

    interface From {
        String topic();
    }

    /**
     * Defines life-cycle events supported by {@linkplain InboundEndpoint}.
     *
     * @author Lei Yang
     * @since 1.0
     */
    sealed interface EventListener {
        @FunctionalInterface
        non-sealed interface ReceivedListener extends EventListener {
            void onReceived(ConsumerRecord<String, String> msg);
        }

        @FunctionalInterface
        non-sealed interface UnmatchedListener extends EventListener {
            void onUnmatched(ConsumerRecord<String, String> msg);
        }

        @FunctionalInterface
        non-sealed interface ExceptionListener extends EventListener {
            void onException(ExceptionContext context);

            interface ExceptionContext {
                Consumer<String, String> consumer();

                ConsumerRecord<String, String> received();

                java.lang.Exception thrown();
            }
        }
    }
}
