package me.ehp246.aufkafka.core.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;

import me.ehp246.aufkafka.api.producer.DirectPartitionMap;
import me.ehp246.aufkafka.api.producer.PartitionMap;
import me.ehp246.aufkafka.api.producer.PartitionMapProvider;
import me.ehp246.aufkafka.api.producer.ProducerConfigProvider;
import me.ehp246.aufkafka.api.producer.ProducerProvider;
import me.ehp246.aufkafka.api.producer.ProducerRecordBuilderProvider;
import me.ehp246.aufkafka.api.producer.SerializedPartitionMap;
import me.ehp246.aufkafka.api.serializer.json.ToJson;
import me.ehp246.aufkafka.core.producer.DefaultProducerRecordBuilder;

/**
 * @author Lei Yang
 *
 */
public final class ProducerConfiguration {
    @Bean
    PartitionMapProvider partitionKeyMapProvider(final BeanFactory beanFactroy) {
        return mapClass -> beanFactroy.getBean(mapClass);
    }

    @Bean
    ProducerRecordBuilderProvider producerRecordBuilderProvider(final ToJson toJson) {
        return (infoProvider, map) -> new DefaultProducerRecordBuilder(infoProvider, map, toJson);
    }

    @Bean
    PartitionMap serializPartitionKeyMap() {
        return new SerializedPartitionMap();
    }

    @Bean
    PartitionMap directPartitionMap() {
        return new DirectPartitionMap();
    }

    @Bean
    ProducerProvider producerProvider(final ProducerConfigProvider configProvider) {
        final var cache = new ConcurrentHashMap<String, Map<String, Object>>();

        return name -> new KafkaProducer<String, String>(cache.computeIfAbsent(name, n -> {
            final var configMap = new HashMap<>(configProvider.get(n));

            configMap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                    StringSerializer.class.getName());
            configMap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                    StringSerializer.class.getName());

            return configMap;
        }));
    }
}
