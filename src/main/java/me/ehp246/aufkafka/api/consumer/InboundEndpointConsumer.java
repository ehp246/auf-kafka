package me.ehp246.aufkafka.api.consumer;

import java.util.concurrent.CompletableFuture;

/**
 * @author Lei Yang
 */
public interface InboundEndpointConsumer {
    CompletableFuture<Boolean> close();

    public sealed interface Listener {
	non-sealed interface ExceptionListener extends Listener {
	    void onException(InboundEndpointConsumer consumer, Exception thrown);
	}
    }
}
