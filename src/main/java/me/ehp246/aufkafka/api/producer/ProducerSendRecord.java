package me.ehp246.aufkafka.api.producer;

import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public record ProducerSendRecord(ProducerRecord<String, String> record, CompletableFuture<RecordMetadata> future) {
}