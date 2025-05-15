package me.ehp246.aufkafka.api.consumer;

import me.ehp246.aufkafka.api.annotation.EnableForKafka;

/**
 * 
 * @author Lei Yang
 * @since 1.0
 * @see EnableForKafka.Inbound#unmatchedConsumer()
 */
@FunctionalInterface
public interface UnmatchedConsumer {
    void accept(InboundEvent event);
}