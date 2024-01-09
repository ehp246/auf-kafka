package me.ehp246.aufkafka.api.consumer;

import me.ehp246.aufkafka.core.consumer.ConsumerExceptionListener;

/**
 * @author Lei Yang
 * @since 1.0
 */
public interface InboundEndpoint {
    From from();

    InvocableKeyRegistry keyRegistry();

    default String name() {
        return null;
    }

    default String consumerConfigName() {
        return null;
    }

    default boolean autoStartup() {
        return true;
    }

    default InvocationListener invocationListener() {
        return null;
    }

    default MsgListener defaultMsgListener() {
        return null;
    }

    default ConsumerExceptionListener consumerExceptionListener() {
        return null;
    }

    interface From {
        String topic();
    }
}
