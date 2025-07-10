package me.ehp246.aufkafka.core.producer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.BeanFactory;
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
            final ProducerRecordBuilder recordBuilder, final BeanFactory beanFactory) {
        return new DefaultProducerFnProvider(KafkaProducer::new, configProvider, recordBuilder,
                name -> beanFactory.getBean(name, Callback.class));
    }
}
