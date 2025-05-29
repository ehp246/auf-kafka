package me.ehp246.aufkafka.api.producer;

import java.util.Map;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ProducerFnProvider {
	ProducerFn get(ProducerFnConfig config);

	record ProducerFnConfig(String producerName, Class<? extends PartitionFn> partitionMapType,
			Map<String, Object> producerProperties) {
		public ProducerFnConfig(String producerConfigName, Class<? extends PartitionFn> partitionMapType) {
			this(producerConfigName, partitionMapType, Map.of());
		}
	}
}
