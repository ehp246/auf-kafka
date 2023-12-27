package me.ehp246.aufkafka.core.configuration;

import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;

import me.ehp246.aufkafka.api.consumer.InboundConsumerExecutorProvider;
import me.ehp246.aufkafka.api.consumer.MsgConsumer;
import me.ehp246.aufkafka.api.consumer.NoopConsumer;

/**
 * @author Lei Yang
 *
 */
public final class ConsumerConfiguration {

    @Bean("e9c593e2-37c6-48e2-8a76-67540e44e3b1")
    public MsgConsumer noopConsumer() {
        return new NoopConsumer();
    }

    @Bean
    public InboundConsumerExecutorProvider executorProvider() {
        return Executors::newVirtualThreadPerTaskExecutor;
    }
}
