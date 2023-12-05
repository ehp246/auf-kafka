package me.ehp246.aufkafka.api.producer;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ProducerFnProvider {
    ProducerFn get(ProducerFnConfig config);
    
    record ProducerFnConfig(String producerConfigName, String paritionKeyMapName) {
    }
}
