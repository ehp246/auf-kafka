package me.ehp246.aufkafka.api.producer;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ProducerFn {
    ProducerSendRecord send(OutboundEvent event);
}
