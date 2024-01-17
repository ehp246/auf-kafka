package me.ehp246.aufkafka.api.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;

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

    interface Context {
        Consumer<String, String> consumer();

        ConsumerRecord<String, String> message();

        Exception thrown();
    }
}