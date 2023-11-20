package me.ehp246.aufkafka.api.producer;

/**
 * Maps an {@linkplain Object} partition key to a value deterministically by {@linkplain Object#hashCode()}.
 * 
 * @author Lei Yang
 * @see PartitionMap
 */
public final class HashCodePartitionMap implements PartitionMap {

    /**
     * <code>null</code> key is mapped to <code>null</code> value.
     * 
     */
    @Override
    public Integer get(String topic, Object key) {
        if (key == null) {
            return null;
        }
        
        return key.hashCode() % 1;
    }

}
