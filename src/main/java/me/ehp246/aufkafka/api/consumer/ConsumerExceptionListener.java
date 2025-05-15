package me.ehp246.aufkafka.api.consumer;

import org.apache.kafka.clients.consumer.Consumer;

import me.ehp246.aufkafka.api.annotation.EnableForKafka;

/**
 * 
 * @author Lei Yang
 * @since 1.0
 * @see EnableForKafka.Inbound#consumerExceptionListener()
 */
@FunctionalInterface
public interface ConsumerExceptionListener {
    void onException(ConsumerExceptionListener.Context context);

    record Context(Consumer<String, String> consumer, InboundEvent event, Exception thrown) {
    }
}