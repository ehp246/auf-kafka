package me.ehp246.test.embedded.consumer.basic;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.kafka.clients.consumer.ConsumerRecords;

import me.ehp246.aufkafka.core.consumer.ConsumerProvider;

/**
 * @author Lei Yang
 *
 */
class ConsumerExecutor {
    private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();

    private final ConsumerProvider consumerProvider;

    private final AtomicReference<CompletableFuture<ConsumerRecords<String, String>>> recRef = new AtomicReference<>(
            new CompletableFuture<ConsumerRecords<String, String>>());

    ConsumerExecutor(final ConsumerProvider consumerProvider) {
        super();
        this.consumerProvider = consumerProvider;
    }

    void startPolling() {
        this.recRef.set(new CompletableFuture<ConsumerRecords<String, String>>());
        this.executor.execute(() -> {
            try (final var consumer = this.consumerProvider.get("")) {
                consumer.subscribe(Set.of("embedded"));
                this.recRef.get().complete(consumer.poll(Duration.ofSeconds(100)));
            }
        });
    }

    ConsumerRecords<String, String> waitAndTake() {
        try {
            final var consumerRecords = this.recRef.get().get();
            this.recRef.set(new CompletableFuture<ConsumerRecords<String, String>>());
            return consumerRecords;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException();
        }
    }
}
