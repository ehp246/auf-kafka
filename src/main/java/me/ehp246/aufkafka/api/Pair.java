package me.ehp246.aufkafka.api;

import org.apache.kafka.common.header.Header;

import me.ehp246.aufkafka.api.producer.OutboundRecord;

/**
 * A key/value pair.
 * 
 * @author Lei Yang
 * @since 1.0
 * @see Header
 */
public record Pair<T>(String key, T value) implements OutboundRecord.Header {
}
