package me.ehp246.aufkafka.api.producer;

/**
 * A mapper that assumes the partition key is an {@linkplain Integer} and returns it as the value.
 * 
 * @author Lei Yang
 * @see PartitionMap
 */
public final class SimpleValuePartitionMap implements PartitionMap {

    /**
     * <code>null</code> is returned as-is.
     */
    @Override
    public Integer get(String topic, Object key) {
        return key == null ? null : (Integer) key;
    }

}
