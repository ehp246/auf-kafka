package me.ehp246.aufkafka.api.producer;

import java.util.List;

import org.apache.kafka.common.PartitionInfo;

/**
 * A mapper that assumes the partition key is an {@linkplain Integer} and returns it as the value.
 * 
 * @author Lei Yang
 * @see PartitionFn
 */
public final class DirectPartitionMap implements PartitionFn {

    /**
     * <code>null</code> is returned as-is.
     */
    @Override
    public Integer apply(List<PartitionInfo> infos, Object key) {
        return key == null ? null : (Integer) key;
    }

}
