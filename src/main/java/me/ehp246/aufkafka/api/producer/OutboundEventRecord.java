package me.ehp246.aufkafka.api.producer;

import java.time.Instant;
import java.util.List;

import me.ehp246.aufkafka.api.serializer.ObjectOf;

public record OutboundEventRecord(String topic, String key, Integer partition, Object value, ObjectOf<?> objectOf,
	Instant timestamp, List<Header> headers) implements OutboundEvent {

    public record HeaderRecord(String key, Object value) implements OutboundEvent.Header {
    }
}
