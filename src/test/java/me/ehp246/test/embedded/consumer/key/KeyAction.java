package me.ehp246.test.embedded.consumer.key;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import me.ehp246.aufkafka.api.annotation.Execution;
import me.ehp246.aufkafka.api.annotation.ForKey;
import me.ehp246.aufkafka.api.annotation.OfKey;
import me.ehp246.aufkafka.api.consumer.InstanceScope;

/**
 * @author Lei Yang
 *
 */
@ForKey(value = ".*", execution = @Execution(scope = InstanceScope.BEAN))
public class KeyAction {
    private final AtomicReference<CompletableFuture<String>> ref = new AtomicReference<>(new CompletableFuture<>());

    public void apply(@OfKey final String key) {
        ref.get().complete(key);
    }

    public String take() {
        final String key;

        try {
            key = ref.get().get();
            reset();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return key;
    }

    public CompletableFuture<String> future() {
        return this.ref.get();
    }

    public void reset() {
        ref.set(new CompletableFuture<String>());
    }
}