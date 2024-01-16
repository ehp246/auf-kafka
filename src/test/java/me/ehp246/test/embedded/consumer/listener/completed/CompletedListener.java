package me.ehp246.test.embedded.consumer.listener.completed;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import me.ehp246.aufkafka.api.consumer.BoundInvocable;
import me.ehp246.aufkafka.api.consumer.InvocationListener;
import me.ehp246.aufkafka.api.consumer.InvocationListener.InvokingListener;
import me.ehp246.aufkafka.api.consumer.Invoked.Completed;

/**
 * @author Lei Yang
 *
 */
class CompletedListener implements InvokingListener, InvocationListener.CompletedListener {
    private final AtomicReference<CompletableFuture<BoundInvocable>> boundRef = new AtomicReference<>(
            new CompletableFuture<>());

    private final AtomicReference<CompletableFuture<Completed>> completedRef = new AtomicReference<>(
            new CompletableFuture<>());

    void reset() {
        this.completedRef.set(new CompletableFuture<>());
        this.boundRef.set(new CompletableFuture<>());
    }

    @Override
    public void onCompleted(final Completed completed) {
        this.completedRef.get().complete(completed);
    }

    @Override
    public void onInvoking(final BoundInvocable bound) {
        this.boundRef.get().complete(bound);
    }

    Completed takeCompleted() {
        final Completed completed;
        try {
            completed = this.completedRef.get().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        this.completedRef.set(new CompletableFuture<>());

        return completed;
    }

    BoundInvocable takeBound() {
        final BoundInvocable bound;
        try {
            bound = this.boundRef.get().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        this.boundRef.set(new CompletableFuture<>());

        return bound;
    }
}
