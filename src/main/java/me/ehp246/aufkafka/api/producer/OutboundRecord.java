package me.ehp246.aufkafka.api.producer;

import java.time.Instant;

/**
 * @author Lei Yang
 *
 */
public interface OutboundRecord {
    String topic();

    default String key() {
        return null;
    }

    default Object partitionKey() {
        return null;
    }

    default Object value() {
        return null;
    }

    default Instant timestamp() {
        return null;
    }
}
