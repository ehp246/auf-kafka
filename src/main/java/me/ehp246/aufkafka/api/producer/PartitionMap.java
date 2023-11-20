package me.ehp246.aufkafka.api.producer;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface PartitionMap {
    Integer get(String topic, Object key);
}
