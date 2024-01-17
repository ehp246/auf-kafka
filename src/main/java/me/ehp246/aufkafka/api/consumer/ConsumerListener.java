package me.ehp246.aufkafka.api.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Defines inbound message life-cycle events supported by
 * {@linkplain InboundEndpoint}.
 *
 * @author Lei Yang
 * @since 1.0
 */
public sealed interface ConsumerListener {
    @FunctionalInterface
    non-sealed interface DispatchingListener extends ConsumerListener {
        void onDispatching(ConsumerRecord<String, String> message);
    }
}