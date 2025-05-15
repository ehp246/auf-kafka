package me.ehp246.aufkafka.api.exception;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import me.ehp246.aufkafka.api.consumer.InboundEndpoint;

/**
 * 
 * @author Lei Yang
 * @since 1.0
 * @see InboundEndpoint#invocableRegistry()
 */
public final class UnknownEventException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final ConsumerRecord<?, ?> msg;

    public UnknownEventException(final ConsumerRecord<?, ?> msg) {
        super("Unknown key: " + msg.key());
        this.msg = msg;
    }

    public ConsumerRecord<?, ?> msg() {
        return this.msg;
    }
}
