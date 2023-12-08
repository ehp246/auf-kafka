package me.ehp246.aufkafka.api.producer;

import java.time.Instant;

import me.ehp246.aufkafka.core.producer.Pair;

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

    default Iterable<Pair<String, Object>> headers() {
        return null;
    }
}
