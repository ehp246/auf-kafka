package me.ehp246.test.app.consumer.bean;

import java.util.Map;

import org.springframework.context.annotation.Bean;

import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound.From;
import me.ehp246.aufkafka.api.consumer.ConsumerConfigProvider;

/**
 * @author Lei Yang
 *
 */
class AppConfig {
    @EnableForKafka({ @Inbound(@From("topic")) })
    static class Case01 {
        @Bean
        ConsumerConfigProvider consumerProvider() {
            return name -> Map.of();
        }
    }

    @EnableForKafka({ @Inbound(value = @From("topic1"), name = "topic1.consumer"),
            @Inbound(@From("${topic2}")) })
    static class Case02 {
    }
}
