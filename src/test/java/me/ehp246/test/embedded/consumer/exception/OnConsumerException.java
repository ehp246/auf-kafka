package me.ehp246.test.embedded.consumer.exception;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import me.ehp246.aufkafka.api.consumer.InboundEndpoint;

/**
 * @author Lei Yang
 *
 */
class OnConsumerException implements InboundEndpoint.EventListener.ExceptionListener {
    private final AtomicReference<CompletableFuture<ExceptionContext>> ref = new AtomicReference<>(
            new CompletableFuture<>());

    @Override
    public void onException(final ExceptionContext context) {
        ref.get().complete(context);
    }

    ExceptionContext take() {
        final ExceptionContext context;
        try {
            context = ref.get().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        ref.set(new CompletableFuture<>());

        return context;
    }
}
