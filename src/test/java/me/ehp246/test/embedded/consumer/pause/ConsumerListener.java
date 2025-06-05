package me.ehp246.test.embedded.consumer.pause;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import me.ehp246.aufkafka.api.consumer.InboundEndpointConsumer;
import me.ehp246.aufkafka.core.util.OneUtil;

/**
 * @author Lei Yang
 */
class ConsumerListener implements InboundEndpointConsumer.Listener.ExceptionListener {
    private final AtomicReference<CompletableFuture<Exception>> ref = new AtomicReference<>(new CompletableFuture<>());

    @Override
    public void onException(InboundEndpointConsumer consumer, Exception thrown) {
	this.ref.get().complete(thrown);
    }

    public Exception take() {
	final var value = OneUtil.orThrow(this.ref.get()::get);
	this.ref.set(new CompletableFuture<Exception>());
	return value;
    }
}
