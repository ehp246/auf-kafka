package me.ehp246.aufkafka.api.producer;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface PartitionMapProvider {
    PartitionMap get(Class<? extends PartitionMap> mapType);
}
