package me.ehp246.test.embedded.consumer.defaultconsumer.unmatched;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import me.ehp246.aufkafka.api.consumer.InboundEvent;
import me.ehp246.aufkafka.api.consumer.UnmatchedConsumer;

/**
 * @author Lei Yang
 *
 */
class Unmatched implements UnmatchedConsumer {
    private final AtomicReference<CompletableFuture<ConsumerRecord<String, String>>> ref = new AtomicReference<CompletableFuture<ConsumerRecord<String, String>>>(
            new CompletableFuture<>());

    @Override
    public void accept(final InboundEvent event) {
        ref.get().complete(event.consumerRecord());
    }

    ConsumerRecord<String, String> take() {
        final ConsumerRecord<String, String> consumerRecord;
        try {
            consumerRecord = this.ref.get().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        this.ref.set(new CompletableFuture<ConsumerRecord<String, String>>());

        return consumerRecord;
    }

    void reset() {
        this.ref.set(new CompletableFuture<ConsumerRecord<String, String>>());
    }
}
