package me.ehp246.aufkafka.api.producer;

import java.time.Instant;
import java.util.List;

public record OutboundEventRecord(String topic, String key, Integer partition, Object value, Instant timestamp,
        List<Header> headers) implements OutboundEvent {

    public record HeaderRecord(String key, Object value) implements OutboundEvent.Header {
    }

    public static OutboundEventRecord withValue(final String topic, final Object value) {
        return new OutboundEventRecord(topic, null, null, value, null, null);
    }
}
