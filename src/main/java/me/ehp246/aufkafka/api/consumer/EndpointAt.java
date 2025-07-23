package me.ehp246.aufkafka.api.consumer;

import java.util.List;

/**
 * @author Lei Yang
 */
public record EndpointAt(String topic, List<Integer> partitions) {
    public EndpointAt(String topic) {
        this(topic, null);
    }
}