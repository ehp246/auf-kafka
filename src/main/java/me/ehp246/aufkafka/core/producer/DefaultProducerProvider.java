package me.ehp246.aufkafka.core.producer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.ehp246.aufkafka.api.common.AufKafkaConstant;
import me.ehp246.aufkafka.api.producer.ProducerConfigProvider;
import me.ehp246.aufkafka.api.producer.ProducerProvider;

final class DefaultProducerProvider implements ProducerProvider, AutoCloseable {
    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultProducerProvider.class);

    private final Function<Map<String, Object>, Producer<String, String>> producerSupplier;
    private final ProducerConfigProvider configProvider;
    private final Map<String, Producer<String, String>> producers = new ConcurrentHashMap<>();

    DefaultProducerProvider(final Function<Map<String, Object>, Producer<String, String>> producerSupplier,
            final ProducerConfigProvider configProvider) {
        super();
        this.producerSupplier = producerSupplier;
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

            return producerSupplier.apply(configMap);
        });
    }

    @Override
    public void close() throws Exception {
        LOGGER.atTrace().setMessage("Closing producers").log();

        for (final var producer : producers.values()) {
            try (producer) {
            } catch (Exception e) {
                LOGGER.atError().setCause(e).addMarker(AufKafkaConstant.EXCEPTION)
                        .setMessage("Producer failed to close, ignored.").log();
            }
        }
        this.producers.clear();
    }
}
