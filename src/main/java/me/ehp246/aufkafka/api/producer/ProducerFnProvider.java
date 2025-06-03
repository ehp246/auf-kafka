package me.ehp246.aufkafka.api.producer;

import java.util.function.Supplier;

import org.apache.kafka.clients.producer.Producer;

/**
 * The abstraction that creates a {@linkplain ProducerFn} which is a highe-level
 * {@linkplain Producer}.
 * 
 * @author Lei Yang
 * @see ProducerFn
 * @see OutboundEvent
 */
@FunctionalInterface
public interface ProducerFnProvider {
    /**
     * Returns a {@linkplain ProducerFn} using the given <code>configName</code>.
     * 
     * @param configName
     */
    ProducerFn get(String configName, Supplier<Boolean> flush);

    default ProducerFn get(String configName) {
	return this.get(configName, Boolean.FALSE::booleanValue);
    }
}
