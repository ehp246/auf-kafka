package me.ehp246.aufkafka.api.producer;

import java.util.Map;

import org.apache.kafka.clients.producer.Producer;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ProducerProvider {
	Producer<String, String> get(String configName, Map<String, Object> custom);
}
