package me.ehp246.aufkafka.api.consumer;

import java.util.Map;

/**
 * @author Lei Yang
 * @since 1.0
 */
public interface InboundEndpoint {
    From from();

    EventInvocableRegistry invocableRegistry();

    default String name() {
        return null;
    }

    default String consumerConfigName() {
        return null;
    }

    /**
     * Defines optional consumer properties.
     * 
     * @return
     */
    default Map<String, Object> consumerProperties() {
        return null;
    }

    default boolean autoStartup() {
        return true;
    }

    default InvocationListener invocationListener() {
        return null;
    }

    default UnmatchedConsumer unmatchedConsumer() {
        return null;
    }

    default ConsumerExceptionListener consumerExceptionListener() {
        return null;
    }

    interface From {
        String topic();
    }
}
