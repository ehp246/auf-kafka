package me.ehp246.aufkafka.core.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Invoked when an exception happens during the consumption of a
 * {@linkplain ConsumerRecord}.
 * 
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface ConsumptionExceptionListener {
    void apply(Context context);

    interface Context {
        Consumer<String, String> consumer();

        ConsumerRecord<String, String> received();

        Exception thrown();
    }
}
