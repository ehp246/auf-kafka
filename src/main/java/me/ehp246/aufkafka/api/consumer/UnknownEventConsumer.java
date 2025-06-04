package me.ehp246.aufkafka.api.consumer;

import me.ehp246.aufkafka.api.annotation.EnableForKafka;

/**
 * Consumes the {@linkplain InboundEvent} for which an
 * {@linkplain EventInvocable} can't be found.
 * 
 * @author Lei Yang
 * @see EnableForKafka.Inbound#unknownEventConsumer()
 */
@FunctionalInterface
public interface UnknownEventConsumer {
    void accept(InboundEvent event);
}