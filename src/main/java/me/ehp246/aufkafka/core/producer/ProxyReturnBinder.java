package me.ehp246.aufkafka.core.producer;

import java.util.concurrent.CompletableFuture;

import me.ehp246.aufkafka.api.producer.OutboundEvent;
import me.ehp246.aufkafka.api.producer.ProducerFn;

/**
 * @author Lei Yang
 *
 */
sealed interface ProxyReturnBinder {
}

@FunctionalInterface
non-sealed interface LocalReturnBinder extends ProxyReturnBinder {
    Object apply(OutboundEvent event, CompletableFuture<ProducerFn.SendRecord> sendFuture);
}
