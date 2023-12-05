package me.ehp246.aufkafka.api.producer;

import java.util.List;

import org.apache.kafka.common.PartitionInfo;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface PartitionKeyMap {
    Integer get(List<PartitionInfo> infos, Object key);
}
