package me.ehp246.aufkafka.api.producer;

import java.util.Map;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ProducerFnProvider {
	ProducerFn get(ProducerFnConfig config);

	record ProducerFnConfig(String producerConfigName, Class<? extends PartitionMap> partitionMapType,
			Map<String, Object> producerProperties) {
		public ProducerFnConfig(String producerConfigName, Class<? extends PartitionMap> partitionMapType) {
			this(producerConfigName, partitionMapType, Map.of());
		}
	}
}
