package me.ehp246.aufkafka.core.producer;

import java.util.concurrent.CompletableFuture;

import me.ehp246.aufkafka.api.producer.ProducerFn;
import me.ehp246.aufkafka.api.producer.ProducerFn.SendRecord;

/**
 * @author Lei Yang
 *
 */
sealed interface ProxyReturnBinder {
}

@FunctionalInterface
non-sealed interface LocalReturnBinder extends ProxyReturnBinder {
    Object apply(CompletableFuture<ProducerFn.SendRecord> sendFuture);
}
