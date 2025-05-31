package me.ehp246.aufkafka.api.producer;

import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ProducerFn {
    CompletableFuture<SendRecord> send(OutboundEvent event);

    record SendRecord(ProducerRecord<String, String> producerRecord, RecordMetadata metadata) {
    }
}
