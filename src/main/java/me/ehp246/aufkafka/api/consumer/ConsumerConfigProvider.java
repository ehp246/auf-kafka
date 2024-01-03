package me.ehp246.aufkafka.api.consumer;

import java.util.Map;

/**
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
