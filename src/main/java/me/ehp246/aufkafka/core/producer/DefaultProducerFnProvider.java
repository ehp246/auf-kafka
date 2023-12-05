package me.ehp246.aufkafka.core.producer;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import me.ehp246.aufkafka.api.producer.PartitionKeyMap;
import me.ehp246.aufkafka.api.producer.PartitionKeyMapProvider;
import me.ehp246.aufkafka.api.producer.ProducerConfigProvider;
import me.ehp246.aufkafka.api.producer.ProducerFn;
import me.ehp246.aufkafka.api.producer.ProducerFn.Sent;
import me.ehp246.aufkafka.api.producer.ProducerFnProvider;

/**
 * @author Lei Yang
 *
 */
public final class DefaultProducerFnProvider implements ProducerFnProvider {
    private final ProducerConfigProvider producerConfigProvider;
    private final PartitionKeyMapProvider partitionKeyMapProvider;

    DefaultProducerFnProvider(final ProducerConfigProvider producerConfigProvider, final PartitionKeyMapProvider partitionKeyMapProvider) {
        super();
        this.producerConfigProvider = producerConfigProvider;
        this.partitionKeyMapProvider = partitionKeyMapProvider;
    }

    @Override
    public ProducerFn get(final ProducerFnConfig config) {
        final var producer = new KafkaProducer<String, String>(
                producerConfigProvider.get(config.producerConfigName()));
        final var partitionMap = this.partitionKeyMapProvider.get(config.paritionKeyMapName());

        return message -> {
            final var producerRecord = new ProducerRecord<String, String>(message.topic(),
                    partitionMap.get(producer.partitionsFor(message.topic()), message.partitionKey()),
                    Optional.ofNullable(message.timestamp()).map(Instant::toEpochMilli)
                            .orElse(null),
                    message.key(), null, null);
            final var completeableFuture = new CompletableFuture<Sent>();

            producer.send(producerRecord, (metadata, exception) -> {
                if (exception == null) {
                    completeableFuture.complete(new Sent(producerRecord, metadata));
                } else {
                    completeableFuture.completeExceptionally(exception);
                }
            });

            return completeableFuture;
        };
    }

}
