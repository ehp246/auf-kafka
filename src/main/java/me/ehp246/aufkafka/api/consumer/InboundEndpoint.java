package me.ehp246.aufkafka.api.consumer;

/**
 * @author Lei Yang
 * @since 1.0
 */
public interface InboundEndpoint {
    From from();

    default String name() {
        return null;
    }

    default boolean autoStartup() {
        return true;
    }

    interface From {
        String topic();
    }
}
