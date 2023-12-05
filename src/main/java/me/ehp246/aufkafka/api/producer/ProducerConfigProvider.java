package me.ehp246.aufkafka.api.producer;

import java.util.Map;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ProducerConfigProvider {
    Map<String, Object> get(String name);
}
