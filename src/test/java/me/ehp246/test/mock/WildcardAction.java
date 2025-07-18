package me.ehp246.test.mock;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import me.ehp246.aufkafka.api.annotation.Execution;
import me.ehp246.aufkafka.api.annotation.ForKey;
import me.ehp246.aufkafka.api.consumer.InboundEvent;
import me.ehp246.aufkafka.api.consumer.InstanceScope;

/**
 * @author Lei Yang
 *
 */
@ForKey(value = ".*", execution = @Execution(scope = InstanceScope.BEAN))
public class WildcardAction {
    private final AtomicReference<CompletableFuture<InboundEvent>> ref = new AtomicReference<>(
            new CompletableFuture<>());

    public void apply(InboundEvent event) {
        ref.get().complete(event);
    }

    public InboundEvent take() {
        final InboundEvent event;

        try {
            event = ref.get().get();
            reset();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return event;
    }

    public CompletableFuture<InboundEvent> future() {
        return this.ref.get();
    }

    public void reset() {
        ref.set(new CompletableFuture<>());
    }
}