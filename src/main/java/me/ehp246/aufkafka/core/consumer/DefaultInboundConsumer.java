package me.ehp246.aufkafka.core.consumer;

import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;

import me.ehp246.aufkafka.api.consumer.InboundEndpoint;
import me.ehp246.aufkafka.api.consumer.InboundConsumer;

/**
 * @author Lei Yang
 *
 */
public final class DefaultInboundConsumer implements InboundConsumer<String, String> {
    private final Map<String, Object> consumerConfig;

    public DefaultInboundConsumer(Map<String, Object> consumerConfig) {
        super();
        this.consumerConfig = consumerConfig;
    }

    @Override
    public InboundEndpoint inboundEndpoint() {
        return null;
    }

    @Override
    public Consumer<String, String> consumer() {
        return null;
    }
}
