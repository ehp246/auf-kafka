package me.ehp246.aufkafka.core.producer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    private final Map<String, Producer<String, String>> producers = new ConcurrentHashMap<>();

    DefaultProducerProvider(final ProducerConfigProvider configProvider) {
        super();
        this.configProvider = configProvider;
    }

    @Override
    public Producer<String, String> get(String configName, Map<String, Object> custom) {
        if (configName == null) {
            throw new IllegalArgumentException("Configuration name can't be null");
        }
        return producers.computeIfAbsent(configName, name -> {
            // Global provider first.
            final var configMap = new HashMap<>(configProvider.get(name));

            // Custom overwrites global
            if (custom != null) {
                configMap.putAll(custom);
            }

            // Required overwrites all others
            configMap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            configMap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

            return new KafkaProducer<String, String>(configMap);
        });
    }

    @Override
    public void close() throws Exception {
        LOGGER.atTrace().setMessage("Closing producers").log();

        for (final var producer : producers.values()) {
            producer.close();
        }
        this.producers.clear();
    }
}
