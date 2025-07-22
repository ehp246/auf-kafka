package me.ehp246.aufkafka.api.consumer;

import java.util.concurrent.CompletableFuture;

/**
 * @author Lei Yang
 */
public interface InboundEndpointConsumer {
    CompletableFuture<Void> close();
}
