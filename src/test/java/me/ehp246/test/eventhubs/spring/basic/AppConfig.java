package me.ehp246.test.eventhubs.spring.basic;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

/**
 * @author Lei Yang
 *
 */
@EnableKafka
class AppConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.properties.sasl.jaas.config}")
    private String jaasConfig;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            final ConsumerFactory<String, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(4);
        return factory;
    }

    @Bean
    ConsumerFactory<String, String> consumerFactory() {
        final Map<String, Object> configMap = new HashMap<>();
        configMap.put("sasl.jaas.config", jaasConfig);
        configMap.put("security.protocol", "SASL_SSL");
        configMap.put("sasl.mechanism", "PLAIN");
        configMap.put("group.id", "aufkafka-local-eventhubs-local-consumer-1");
        configMap.put("request.timeout.ms", "60000");
        configMap.put(ConsumerConfig.CLIENT_ID_CONFIG, "aufkafka-local-eventhubs-consumer");
        configMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        return new DefaultKafkaConsumerFactory<>(configMap);
    }

    @Bean
    ProducerFactory<String, String> producerFactory() {
        final Map<String, Object> configMap = new HashMap<>();
        configMap.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configMap.put("sasl.jaas.config", jaasConfig);
        configMap.put("security.protocol", "SASL_SSL");
        configMap.put("sasl.mechanism", "PLAIN");
        configMap.put(ProducerConfig.CLIENT_ID_CONFIG, "aufkafka-local-eventhubs");
        configMap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configMap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return new DefaultKafkaProducerFactory<>(configMap);
    }

    @Bean
    KafkaTemplate<String, String> kafkaTemplate(final ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);

    }
}
