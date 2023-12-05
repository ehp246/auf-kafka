package me.ehp246.aufkafka.core.configuration;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;

import me.ehp246.aufkafka.api.producer.PartitionKeyMap;
import me.ehp246.aufkafka.api.producer.PartitionKeyMapProvider;
import me.ehp246.aufkafka.api.producer.SerializedPartitionMap;
import me.ehp246.aufkafka.api.spi.PropertyResolver;

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
    PartitionKeyMapProvider partitionKeyMapProvider(final BeanFactory beanFactroy) {
        return name -> beanFactroy.getBean(
                name == null || name.isBlank() ? "8166c453-5ec8-472b-a294-844d28694c32" : name,
                PartitionKeyMap.class);
    }

    @Bean("8166c453-5ec8-472b-a294-844d28694c32")
    PartitionKeyMap serializPartitionKeyMap() {
        return new SerializedPartitionMap();
    }
}
