package me.ehp246.test.embedded.consumer.defaultconsumer.unmatched;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import me.ehp246.aufkafka.api.consumer.MsgConsumer;

/**
 * @author Lei Yang
 *
 */
class Unmatched implements MsgConsumer {
    private final AtomicReference<CompletableFuture<ConsumerRecord<String, String>>> ref = new AtomicReference<CompletableFuture<ConsumerRecord<String, String>>>(
            new CompletableFuture<>());

    @Override
    public void apply(final ConsumerRecord<String, String> msg) {
        ref.get().complete(msg);
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
