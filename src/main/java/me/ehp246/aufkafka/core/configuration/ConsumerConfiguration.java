package me.ehp246.aufkafka.core.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import me.ehp246.aufkafka.api.AufKafkaConstant;
import me.ehp246.aufkafka.api.consumer.ConsumerConfigProvider;
import me.ehp246.aufkafka.api.consumer.InboundConsumerExecutorProvider;
import me.ehp246.aufkafka.api.consumer.LoggingDispatchingListener;
import me.ehp246.aufkafka.api.consumer.NoOpUnmatchedConsumer;
import me.ehp246.aufkafka.core.consumer.ConsumerProvider;
import me.ehp246.aufkafka.core.consumer.IgnoringConsumerExceptionListener;

/**
 * @author Lei Yang
 *
 */
public final class ConsumerConfiguration {

    @Bean(AufKafkaConstant.BEAN_NOOP_UNMATCHED_CONSUMER)
    public NoOpUnmatchedConsumer noOpUnmatchedConsumer() {
        return new NoOpUnmatchedConsumer();
    }

    @Bean(AufKafkaConstant.BEAN_LOGGING_DISPATCHING_LISTENER)
    public LoggingDispatchingListener loggingDispatchingListener(
            @Value("${" + AufKafkaConstant.PROPERTY_INBOUND_MESSAGELOGGING_ENABLED
                    + ":false}") final boolean enabled) {
        return new LoggingDispatchingListener(enabled);
    }

    @Bean(AufKafkaConstant.BEAN_IGNORING_CONSUMEREXCEPTION_LISTENER)
    public IgnoringConsumerExceptionListener ignoringConsumer() {
        return new IgnoringConsumerExceptionListener();
    }

    @Bean
    public InboundConsumerExecutorProvider executorProvider() {
        return Executors::newVirtualThreadPerTaskExecutor;
    }

    @Bean
    public ConsumerProvider consumerProvider(final ConsumerConfigProvider configProvider) {
        final var cache = new ConcurrentHashMap<String, Map<String, Object>>();

        return name -> {
            return new KafkaConsumer<String, String>(cache.computeIfAbsent(name, n -> {
                final var configMap = new HashMap<>(configProvider.get(n));

                /*
                 * Mandatory configuration.
                 */
                configMap.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
                configMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                        StringDeserializer.class.getName());
                configMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                        StringDeserializer.class.getName());

                return configMap;
            }));
        };
    }
}
