package me.ehp246.test.embedded.producer.callback;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;

import me.ehp246.aufkafka.core.util.OneUtil;

class Callmeback implements Callback {
    private final AtomicReference<CompletableFuture<RecordMetadata>> ref = new AtomicReference<CompletableFuture<RecordMetadata>>(
            new CompletableFuture<>());

    @Override
    public void onCompletion(RecordMetadata metadata, Exception exception) {
        if (exception != null) {
            this.ref.get().completeExceptionally(exception);
        } else {
            this.ref.get().complete(metadata);
        }
    }

    RecordMetadata take() {
        final var value = OneUtil.orThrow(this.ref.get()::get);
        this.ref.set(new CompletableFuture<RecordMetadata>());
        return value;
    }
}
