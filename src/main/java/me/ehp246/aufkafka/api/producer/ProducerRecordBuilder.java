package me.ehp246.aufkafka.api.producer;

import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * The abstraction that creates a {@linkplain ProducerRecord} from an
 * {@linkplain OutboundEvent}.
 * 
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ProducerRecordBuilder {
    ProducerRecord<String, String> apply(OutboundEvent outboundEvent);
}
