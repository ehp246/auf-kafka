package me.ehp246.aufkafka.api.consumer;

import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;

/**
 * To be implemented by the application as a Spring bean to provide the
 * configuration of {@linkplain Consumer} by a name.
 * 
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface ConsumerConfigProvider {
    /**
     * 
     * @param name Could be <code>null</code> or blank.
     */
    Map<String, Object> get(String name);
}
