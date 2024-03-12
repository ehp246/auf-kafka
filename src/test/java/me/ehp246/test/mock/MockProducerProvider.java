package me.ehp246.test.mock;

import java.util.Map;

import org.apache.kafka.clients.producer.Producer;

import me.ehp246.aufkafka.api.producer.ProducerProvider;

/**
 * @author Lei Yang
 *
 */
public class MockProducerProvider implements ProducerProvider {

	@Override
	public Producer<String, String> get(String name, Map<String, Object> custom) {
		return null;
	}

}
