package me.ehp246.aufkafka.core.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;

import me.ehp246.aufkafka.api.consumer.ConsumerConfigProvider;
import me.ehp246.aufkafka.api.consumer.InboundConsumerExecutorProvider;
import me.ehp246.aufkafka.api.consumer.MsgListener;
import me.ehp246.aufkafka.api.consumer.NoOpMsgFunction;
import me.ehp246.aufkafka.core.consumer.ConsumerProvider;

/**
 * @author Lei Yang
 *
 */
public final class ConsumerConfiguration {

    @Bean("e9c593e2-37c6-48e2-8a76-67540e44e3b1")
    public MsgListener noopConsumer() {
        return new NoOpMsgFunction();
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

                configMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                        StringDeserializer.class.getName());
                configMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                        StringDeserializer.class.getName());

                return configMap;
            }));
        };
    }
}
