package me.ehp246.aufkafka.api.producer;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ProducerProvider {
    Producer<String, String> get(ProducerConfig config);
}
