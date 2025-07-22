package me.ehp246.test.embedded.consumer.exception;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import me.ehp246.aufkafka.api.consumer.DispatchListener;
import me.ehp246.aufkafka.api.consumer.InboundEvent;
import me.ehp246.aufkafka.api.consumer.InboundEventContext;

/**
 * @author Lei Yang
 *
 */
class OnConsumerException implements DispatchListener.ExceptionListener {
    record Context(InboundEvent event, Exception thrown) {
    }

    private final AtomicReference<CompletableFuture<Context>> ref = new AtomicReference<>(new CompletableFuture<>());

    @Override
    public void onException(final InboundEventContext context, final Exception thrown) {
        ref.get().complete(new Context(context.event(), thrown));
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
