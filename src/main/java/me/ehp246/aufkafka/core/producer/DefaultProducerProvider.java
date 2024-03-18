package me.ehp246.aufkafka.core.producer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.ehp246.aufkafka.api.producer.ProducerConfigProvider;
import me.ehp246.aufkafka.api.producer.ProducerProvider;

final class DefaultProducerProvider implements ProducerProvider, AutoCloseable {
    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultProducerProvider.class);

    private final ProducerConfigProvider configProvider;
    private final List<Producer<?, ?>> producers = new ArrayList<>();

    public DefaultProducerProvider(final ProducerConfigProvider configProvider) {
	super();
	this.configProvider = configProvider;
    }

    @Override
    public Producer<String, String> get(String configName, Map<String, Object> custom) {
	// Global provider first.
	final var configMap = new HashMap<>(configProvider.get(configName));

	// Custom overwrites global
	if (custom != null) {
	    configMap.putAll(custom);
	}

	// Required overwrites all others
	configMap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
	configMap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

	final var kafkaProducer = new KafkaProducer<String, String>(configMap);
	producers.add(kafkaProducer);

	return kafkaProducer;
    }

    @Override
    public void close() throws Exception {
	LOGGER.atTrace().setMessage("Closing producers").log();

	for (var producer : producers) {
	    producer.flush();
	    producer.close();
	}
    }
}
