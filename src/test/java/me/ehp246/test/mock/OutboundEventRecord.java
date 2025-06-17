package me.ehp246.test.mock;

import java.time.Instant;
import java.util.List;

import me.ehp246.aufkafka.api.producer.OutboundEvent;
import me.ehp246.aufkafka.api.serializer.jackson.TypeOfJson;

public record OutboundEventRecord(String topic, String key, Integer partition, Object value, TypeOfJson typeOf,
        Instant timestamp, List<Header> headers) implements OutboundEvent {

    public record HeaderRecord(String key, Object value) implements OutboundEvent.Header {
    }

    public static OutboundEventRecord withValue(final String topic, final Object value) {
        return new OutboundEventRecord(topic, null, null, value, null, null, null);
    }

    public static OutboundEventRecord withValueAndType(final String topic, final Object value,
            final TypeOfJson typeOf) {
        return new OutboundEventRecord(topic, null, null, value, typeOf, null, null);
    }
}
