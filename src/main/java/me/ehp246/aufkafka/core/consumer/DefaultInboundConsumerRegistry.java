package me.ehp246.aufkafka.core.consumer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import me.ehp246.aufkafka.api.consumer.InboundConsumerRegistry;
import me.ehp246.aufkafka.api.consumer.InboundEndpointConsumer;

/**
 * @author Lei Yang
 */
final class DefaultInboundConsumerRegistry implements InboundConsumerRegistry {
    private final Map<String, InboundEndpointConsumer> map = new ConcurrentHashMap<>();

    @Override
    public InboundEndpointConsumer get(final String name) {
	return this.map.get(name);
    }

    /**
     * @param name
     * @return existing mapping if present. Otherwise <code>null</code>.
     */
    InboundEndpointConsumer remove(final String name) {
	return this.map.remove(name);
    }

    /**
     * Add the mapping.
     * 
     * @param name
     * @param consumer
     * @return existing mapping if present. Otherwise <code>null</code>.
     */
    InboundEndpointConsumer put(final String name, final InboundEndpointConsumer consumer) {
	return this.map.put(name, consumer);
    }

    /**
     * 
     * @return a copy of the key set.
     */
    Set<String> getNames() {
	return new HashSet<>(this.map.keySet());
    }
}
