package me.ehp246.aufkafka.core.consumer;

import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;

import me.ehp246.aufkafka.core.configuration.ConsumerConfiguration;

/**
 * Internal beans used by {@linkplain InboundEndpointConsumerConfigurer}.
 * 
 * @author Lei Yang
 * @since 1.0
 * @see ConsumerConfiguration#consumerProvider(me.ehp246.aufkafka.api.consumer.ConsumerConfigProvider)
 */
@FunctionalInterface
public interface ConsumerProvider {
    Consumer<String, String> get(String configName, Map<String, Object> custom);
}
