package me.ehp246.aufkafka.core.consumer;

import java.util.HashMap;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import me.ehp246.aufkafka.api.common.AufKafkaConstant;
import me.ehp246.aufkafka.api.consumer.ConsumerConfigProvider;
import me.ehp246.aufkafka.api.consumer.InboundConsumerExecutorProvider;
import me.ehp246.aufkafka.api.consumer.InboundDispatchingLogger;
import me.ehp246.aufkafka.api.consumer.NoOpUnknownListener;

/**
 * Expects an application-provided {@linkplain ConsumerConfigProvider} bean.
 * 
 * @author Lei Yang
 * @since 1.0
 * @see ConsumerConfigProvider
 */
public final class ConsumerConfiguration {

    @Bean(AufKafkaConstant.BEAN_NOOP_UNKNOWN_EVENT_LISTENER)
    NoOpUnknownListener noOpUnknownListener() {
        return new NoOpUnknownListener();
    }

    @Bean(AufKafkaConstant.BEAN_LOGGING_DISPATCHING_LISTENER)
    InboundDispatchingLogger inboundDispatchingLogger(
            @Value("${" + AufKafkaConstant.PROPERTY_INBOUND_MESSAGELOGGING_ENABLED + ":false}") final boolean enabled) {
        return new InboundDispatchingLogger(enabled);
    }

    @Bean(AufKafkaConstant.BEAN_IGNORING_DISPATCHING_EXCEPTION_LISTENER)
    IgnoringConsumerExceptionListener ignoringConsumer() {
        return new IgnoringConsumerExceptionListener();
    }

    @Bean
    InboundConsumerExecutorProvider executorProvider() {
        return Executors::newVirtualThreadPerTaskExecutor;
    }

    @Bean("6156055b-0334-48aa-a1c5-e42507128b33")
    ConsumerProvider consumerProvider(final ConsumerConfigProvider configProvider) {
        return (name, custom) -> {
            final var configMap = new HashMap<>(configProvider.get(name));

            /*
             * Custom configuration first.
             */
            if (custom != null) {
                configMap.putAll(custom);
            }

            if (configMap.get(ConsumerConfig.MAX_POLL_RECORDS_CONFIG) == null) {
                configMap.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
            }
            /*
             * Mandatory configuration overwriting any custom ones.
             */
            configMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
            configMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            configMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

            return new KafkaConsumer<String, String>(configMap);
        };
    }

    @Bean
    DefaultInboundEndpointConsumerRegistry inboundEndpointConsumerRegistry() {
        return new DefaultInboundEndpointConsumerRegistry();
    }
}
