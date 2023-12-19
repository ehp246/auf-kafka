package me.ehp246.test.mock;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import me.ehp246.aufkafka.api.annotation.ForKey;

/**
 * @author Lei Yang
 *
 */
@ForKey(".*")
public class WildcardAction {
    private final AtomicReference<CompletableFuture<ConsumerRecord<String, String>>> ref = new AtomicReference<>(
            new CompletableFuture<>());

    public void apply(ConsumerRecord<String, String> msg) {
        ref.get().complete(msg);
    }

    public ConsumerRecord<String, String> take() {
        ConsumerRecord<String, String> consumerRecord;

        try {
            consumerRecord = ref.get().get();
            reset();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return consumerRecord;
    }

    public void reset() {
        ref.set(new CompletableFuture<ConsumerRecord<String, String>>());
    }
}