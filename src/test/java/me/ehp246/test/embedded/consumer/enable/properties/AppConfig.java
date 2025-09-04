package me.ehp246.test.embedded.consumer.enable.properties;

import org.springframework.kafka.annotation.EnableKafka;

import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound.At;

/**
 * @author Lei Yang
 *
 */
@EnableKafka
@EnableForKafka({ @Inbound(value = @At(AppConfig.TOPIC)),
        @Inbound(value = @At(AppConfig.TOPIC), consumerProperties = { "custom1", "value1", "custom2", "${value.2}" }) })
class AppConfig {
    final static String TOPIC = "f45bbb4d-c997-478d-9fda-fbf481eec642";
}
