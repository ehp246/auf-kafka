package me.ehp246.test.embedded.consumer.exception;

import org.springframework.context.annotation.Bean;

import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound.At;
import me.ehp246.aufkafka.api.consumer.InvocationListener.FailedListener;

/**
 * @author Lei Yang
 *
 */
@EnableForKafka({ @Inbound(value = @At("embedded"), invocationListener = "onFailed",
        dispatchExceptionListener = "onConsumerException") })
class AppConfig {

    @Bean
    FailedListener onFailed() {
        return failed -> {
            throw new NullPointerException(failed.thrown().getMessage());
        };
    }

    @Bean
    OnConsumerException onConsumerException() {
        return new OnConsumerException();
    }
}
