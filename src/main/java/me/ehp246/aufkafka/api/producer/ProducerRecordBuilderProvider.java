package me.ehp246.aufkafka.api.producer;

import java.util.List;
import java.util.function.Function;

import org.apache.kafka.common.PartitionInfo;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ProducerRecordBuilderProvider {
    ProducerRecordBuilder apply(Function<String, List<PartitionInfo>> paritionInfoProvider,
            PartitionMap partitionMap);
}
