package me.ehp246.aufkafka.core.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface ConsumerExceptionListener {
    void apply(ConsumerExceptionContext context);

    record ConsumerExceptionContext(Consumer<String, String> consumer,
            ConsumerRecord<String, String> msg, Exception exception) {
    }
}
