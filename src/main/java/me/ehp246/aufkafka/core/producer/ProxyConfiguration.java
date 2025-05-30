package me.ehp246.aufkafka.core.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.context.annotation.Bean;

import me.ehp246.aufkafka.api.producer.ProducerConfigProvider;
import me.ehp246.aufkafka.api.producer.ProducerFnProvider;
import me.ehp246.aufkafka.api.producer.ProducerRecordBuilder;

/**
 * @author Lei Yang
 *
 */
public final class ProxyConfiguration {

    @Bean
    ProducerFnProvider producerFnProvider(final ProducerConfigProvider configProvider,
            final ProducerRecordBuilder recordBuilder) {
        return new DefaultProducerFnProvider(KafkaProducer::new, configProvider, recordBuilder);
    }
}
