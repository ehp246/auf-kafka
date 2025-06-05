package me.ehp246.test.embedded.consumer.pause;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import me.ehp246.aufkafka.api.consumer.ConsumerConfigProvider;
import me.ehp246.aufkafka.api.producer.ProducerConfigProvider;

class EmbeddedKafkaConfig {
    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Bean
    ProducerConfigProvider producerConfigProvider() {
	final Map<String, Object> configMap = KafkaTestUtils.producerProps(embeddedKafka);

	return name -> configMap;
    }

    @Bean
    ConsumerConfigProvider consumerConfigProvider() {
	final Map<String, Object> configMap = KafkaTestUtils.consumerProps("test", "true", embeddedKafka);
	configMap.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, App.MAX_INTERVAL);

	return name -> configMap;
    }
}
