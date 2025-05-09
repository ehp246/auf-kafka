package me.ehp246.aufkafka.core.producer;

import me.ehp246.aufkafka.api.producer.OutboundRecord;

/**
 * @author Lei Yang
 */
public record OutboundHeader(String key, Object value) implements OutboundRecord.Header {

}
