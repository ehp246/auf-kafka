package me.ehp246.aufkafka.api.consumer;

import java.util.concurrent.Executor;

/**
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface InboundConsumerExecutorProvider {
    Executor get();
}
