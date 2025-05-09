package me.ehp246.aufkafka.core.producer;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;

import me.ehp246.aufkafka.api.producer.DirectPartitionMap;
import me.ehp246.aufkafka.api.producer.PartitionMapProvider;
import me.ehp246.aufkafka.api.producer.ProducerConfigProvider;
import me.ehp246.aufkafka.api.producer.ProducerProvider;
import me.ehp246.aufkafka.api.producer.ProducerRecordBuilderProvider;
import me.ehp246.aufkafka.api.producer.SerializedPartitionFn;
import me.ehp246.aufkafka.api.serializer.json.ToJson;

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
    SerializedPartitionFn serializPartitionKeyMap() {
	return new SerializedPartitionFn();
    }

    @Bean
    DirectPartitionMap directPartitionMap() {
	return new DirectPartitionMap();
    }

    @Bean
    ProducerProvider producerProvider(final ProducerConfigProvider configProvider) {
	return new DefaultProducerProvider(configProvider);
    }
}
