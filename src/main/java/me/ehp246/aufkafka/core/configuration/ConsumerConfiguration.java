package me.ehp246.aufkafka.core.configuration;

import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;

import me.ehp246.aufkafka.api.consumer.ConsumerConfigProvider;
import me.ehp246.aufkafka.api.consumer.InboundConsumerExecutorProvider;
import me.ehp246.aufkafka.api.consumer.MsgConsumer;
import me.ehp246.aufkafka.api.consumer.NoopConsumer;
import me.ehp246.aufkafka.core.consumer.ConsumerProvider;

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

    @Bean
    public ConsumerProvider consumerProvider(final ConsumerConfigProvider configProvider) {
        return name -> {
            final var configMap = configProvider.get(name);

            configMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                    StringDeserializer.class.getName());
            configMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                    StringDeserializer.class.getName());

            return new KafkaConsumer<String, String>(configMap);
        };
    }
}
