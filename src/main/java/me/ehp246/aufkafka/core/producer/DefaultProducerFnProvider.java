package me.ehp246.aufkafka.core.producer;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

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

    DefaultProducerFnProvider(final ProducerConfigProvider producerConfigProvider) {
        super();
        this.producerConfigProvider = producerConfigProvider;
    }

    @Override
    public ProducerFn get(final String name) {
        final var producer = new KafkaProducer<String, String>(producerConfigProvider.get(name));

        return message -> {
            final var producerRecord = new ProducerRecord<String, String>(message.topic(), message.partition(),
                    Optional.ofNullable(message.instant()).map(Instant::toEpochMilli).orElse(null), message.key(),
                    null, null);
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
