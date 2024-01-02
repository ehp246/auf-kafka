package me.ehp246.test.app.consumer.bean;

import org.apache.kafka.clients.consumer.Consumer;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;

import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound.From;
import me.ehp246.aufkafka.api.consumer.ConsumerProvider;

/**
 * @author Lei Yang
 *
 */
class AppConfig {
    @EnableForKafka({ @Inbound(@From("topic")) })
    static class Case01 {
        @SuppressWarnings("unchecked")
        @Bean
        ConsumerProvider consumerProvider() {
            return name -> Mockito.mock(Consumer.class);
        }
    }

    @EnableForKafka({ @Inbound(value = @From("topic1"), name = "topic1.consumer"),
            @Inbound(@From("${topic2}")) })
    static class Case02 {
    }
}
