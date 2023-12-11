package me.ehp246.aufkafka.api.producer;

import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ProducerRecordBuilder {
    ProducerRecord<String, String> apply(OutboundRecord outboundRecord);
}
