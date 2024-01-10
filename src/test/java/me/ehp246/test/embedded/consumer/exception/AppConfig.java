package me.ehp246.test.embedded.consumer.exception;

import org.springframework.context.annotation.Bean;

import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound.From;
import me.ehp246.aufkafka.api.consumer.InvocationListener.OnFailed;

/**
 * @author Lei Yang
 *
 */
@EnableForKafka({ @Inbound(value = @From("embedded"), invocationListener = "onFailed",
        consumptionExceptionListener = "onConsumerException") })
class AppConfig {

    @Bean
    OnFailed onFailed() {
        return failed -> {
            throw new NullPointerException(failed.thrown().getMessage());
        };
    }

    @Bean
    OnConsumerException onConsumerException() {
        return new OnConsumerException();
    }
}
