package me.ehp246.aufkafka.core.configuration;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;

import me.ehp246.aufkafka.api.producer.DirectPartitionMap;
import me.ehp246.aufkafka.api.producer.PartitionMap;
import me.ehp246.aufkafka.api.producer.PartitionMapProvider;
import me.ehp246.aufkafka.api.producer.ProducerRecordBuilderProvider;
import me.ehp246.aufkafka.api.producer.SerializedPartitionMap;
import me.ehp246.aufkafka.api.spi.PropertyResolver;
import me.ehp246.aufkafka.core.producer.DefaultProducerRecordBuilder;

/**
 * @author Lei Yang
 *
 */
public final class AufKafkaConfiguration {
    @Bean
    PropertyResolver propertyResolver(
            final org.springframework.core.env.PropertyResolver springResolver) {
        return springResolver::resolveRequiredPlaceholders;
    }

    @Bean
    PartitionMapProvider partitionKeyMapProvider(final BeanFactory beanFactroy) {
        return mapClass -> beanFactroy.getBean(mapClass);
    }

    @Bean
    ProducerRecordBuilderProvider producerRecordBuilderProvider() {
        return (infoProvider, map) -> new DefaultProducerRecordBuilder(infoProvider, map);
    }

    @Bean
    PartitionMap serializPartitionKeyMap() {
        return new SerializedPartitionMap();
    }

    @Bean
    PartitionMap directPartitionMap() {
        return new DirectPartitionMap();
    }
}
