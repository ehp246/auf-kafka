package me.ehp246.aufkafka.api.exception;

import me.ehp246.aufkafka.api.consumer.InboundEndpoint;
import me.ehp246.aufkafka.api.consumer.InboundEvent;

/**
 * 
 * @author Lei Yang
 * @since 1.0
 * @see InboundEndpoint#invocableRegistry()
 */
public final class UnknownEventException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final InboundEvent event;

    public UnknownEventException(final InboundEvent event) {
        this.event = event;
    }

    public InboundEvent event() {
        return this.event;
    }
}
