package me.ehp246.aufkafka.api.consumer;

/**
 * @author Lei Yang
 *
 */
public interface InboundConsumerRegistrar {
    InboundConsumer get(String name);

    InboundConsumerRegistrar put(InboundConsumer inboundConsumer);
}
