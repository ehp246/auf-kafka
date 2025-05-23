package me.ehp246.aufkafka.api.producer;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface PartitionMapProvider {
    PartitionFn get(Class<? extends PartitionFn> mapType);
}
