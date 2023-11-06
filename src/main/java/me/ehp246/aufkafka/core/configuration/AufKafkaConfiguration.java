package me.ehp246.aufkafka.core.configuration;

import org.springframework.context.annotation.Bean;

import me.ehp246.aufkafka.api.spi.PropertyResolver;

/**
 * @author Lei Yang
 *
 */
public final class AufKafkaConfiguration {
    @Bean
    PropertyResolver propertyResolver(final org.springframework.core.env.PropertyResolver springResolver) {
        return springResolver::resolveRequiredPlaceholders;
    }
}
