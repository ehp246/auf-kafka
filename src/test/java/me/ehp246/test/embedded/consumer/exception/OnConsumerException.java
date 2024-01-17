package me.ehp246.test.embedded.consumer.exception;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import me.ehp246.aufkafka.api.consumer.ConsumerExceptionListener;

/**
 * @author Lei Yang
 *
 */
class OnConsumerException implements ConsumerExceptionListener {
    private final AtomicReference<CompletableFuture<Context>> ref = new AtomicReference<>(
            new CompletableFuture<>());

    @Override
    public void onException(final Context context) {
        ref.get().complete(context);
    }

    Context take() {
        final Context context;
        try {
            context = ref.get().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        ref.set(new CompletableFuture<>());

        return context;
    }
}
