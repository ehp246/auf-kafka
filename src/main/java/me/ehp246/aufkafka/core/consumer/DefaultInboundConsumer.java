package me.ehp246.aufkafka.core.consumer;

import org.apache.kafka.clients.consumer.Consumer;

import me.ehp246.aufkafka.api.consumer.InboundConsumer;
import me.ehp246.aufkafka.api.consumer.InboundEndpoint;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class DefaultInboundConsumer implements InboundConsumer {
    private final InboundEndpoint endpoint;
    private final Consumer<String, String> consumer;

    public DefaultInboundConsumer(final InboundEndpoint endpoint,
            final Consumer<String, String> consumer) {
        super();
        this.endpoint = endpoint;
        this.consumer = consumer;
    }

    public void start() {

    }

    @Override
    public InboundEndpoint inboundEndpoint() {
        return this.endpoint;
    }

    @Override
    public Consumer<String, String> consumer() {
        return this.consumer;
    }
}
