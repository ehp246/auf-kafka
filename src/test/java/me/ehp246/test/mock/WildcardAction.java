package me.ehp246.test.mock;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
    private volatile CompletableFuture<InboundEvent> ref = new CompletableFuture<>();

    public void apply(InboundEvent event) {
        ref.complete(event);
    }

    public InboundEvent take() {
        final InboundEvent event;

        try {
            event = ref.get();
            reset();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return event;
    }

    public CompletableFuture<InboundEvent> future() {
        return this.ref;
    }

    public void reset() {
        this.ref = new CompletableFuture<>();
    }
}