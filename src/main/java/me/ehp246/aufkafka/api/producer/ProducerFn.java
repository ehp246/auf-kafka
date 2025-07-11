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
    ProducerFnRecord send(OutboundEvent event);

    public record ProducerFnRecord(ProducerRecord<String, String> record, CompletableFuture<RecordMetadata> future) {
    }
}
