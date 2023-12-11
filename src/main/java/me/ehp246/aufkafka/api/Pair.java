package me.ehp246.aufkafka.api;

import me.ehp246.aufkafka.api.producer.OutboundRecord;

/**
 * A name/value pair.
 * 
 * @author Lei Yang
 * @since 1.0
 */
public record Pair<T>(String name, T value) implements OutboundRecord.Header {
}
