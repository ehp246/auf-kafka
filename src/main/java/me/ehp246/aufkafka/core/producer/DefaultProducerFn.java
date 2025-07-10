package me.ehp246.aufkafka.core.producer;

import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.RecordMetadata;

import me.ehp246.aufkafka.api.producer.OutboundEvent;
import me.ehp246.aufkafka.api.producer.ProducerFn;
import me.ehp246.aufkafka.api.producer.ProducerRecordBuilder;

/**
 * @author Lei Yang
 */
public final class DefaultProducerFn implements ProducerFn, AutoCloseable {

    private final ProducerRecordBuilder recordBuilder;
    private final Producer<String, String> producer;
    private final boolean flush;
    private final Callback callback;

    public DefaultProducerFn(final ProducerRecordBuilder recordBuilder, final Producer<String, String> producer,
            final boolean flush, final Callback callback) {
        super();
        this.recordBuilder = recordBuilder;
        this.producer = producer;
        this.flush = flush;
        this.callback = callback;
    }

    @Override
    public SendRecord send(OutboundEvent event) {
        final var producerRecord = recordBuilder.apply(event);
        final var future = new CompletableFuture<RecordMetadata>();

        producer.send(producerRecord, (metadata, exception) -> {
            if (callback != null) {
                callback.onCompletion(metadata, exception);
            }

            if (exception == null) {
                future.complete(metadata);
            } else {
                future.completeExceptionally(exception);
            }
        });

        if (flush) {
            producer.flush();
        }

        return new SendRecord(producerRecord, future);
    }

    @Override
    public void close() throws Exception {
        producer.close();
    }
}
