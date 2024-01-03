package me.ehp246.test.mock;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import me.ehp246.aufkafka.api.consumer.ConsumerConfigProvider;
import me.ehp246.aufkafka.api.producer.ProducerConfigProvider;

/**
 * @author Lei Yang
 *
 */
public final class EmbeddedKafkaConfig {
    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            final ConsumerFactory<String, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(1);
        return factory;
    }

    @Bean
    ConsumerFactory<String, String> consumerFactory() {
        final var configMap = KafkaTestUtils.consumerProps("test", "true", embeddedKafka);
        configMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        configMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        configMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(configMap);
    }

    @Bean
    ProducerConfigProvider producerConfigProvider() {
        final Map<String, Object> configMap = KafkaTestUtils.producerProps(embeddedKafka);

        return name -> configMap;
    }

    @Bean
    ConsumerConfigProvider consumerConfigProvider() {
        final Map<String, Object> configMap = KafkaTestUtils.consumerProps("test", "true",
                embeddedKafka);

        return name -> configMap;
    }

    @Bean
    ProducerFactory<String, String> producerFactory() {
        final Map<String, Object> configMap = KafkaTestUtils.producerProps(embeddedKafka);
        configMap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configMap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());

        return new DefaultKafkaProducerFactory<>(configMap);
    }

    @Bean
    KafkaTemplate<String, String> kafkaTemplate(
            final ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
