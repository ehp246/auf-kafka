package me.ehp246.aufkafka.api.consumer;

import java.time.Duration;
import java.util.List;
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

    default String configName() {
        return null;
    }

    /**
     * Defines optional consumer properties.
     */
    default Map<String, Object> consumerProperties() {
        return null;
    }

    default boolean autoStartup() {
        return true;
    }

    default Duration pollDuration() {
        return Duration.ofMillis(100);
    }

    default InvocationListener invocationListener() {
        return null;
    }

    default DispatchListener.UnknownEventListener unknownEventListener() {
        return null;
    }

    default DispatchListener.ExceptionListener dispatchExceptionListener() {
        return null;
    }

    interface From {
        String topic();

        default List<Integer> partitions() {
            return null;
        }
    }
}
