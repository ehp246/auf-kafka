package me.ehp246.aufkafka.api.consumer;

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

    default boolean autoStartup() {
        return true;
    }

    default InvocationListener invocationListener() {
        return null;
    }

    default MsgConsumer defaultConsumer() {
        return null;
    }

    interface From {
        String topic();
    }
}