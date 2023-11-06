package me.ehp246.aufkafka.api.producer;

import java.time.Instant;

/**
 * @author Lei Yang
 *
 */
public interface OutboundMessage {
    String topic();

    default Integer partition() {
        return null;
    }

    default String key() {
        return null;
    }

    default Object value() {
        return null;
    }

    default Instant instant() {
        return null;
    }
}
