package me.ehp246.test.embedded.consumer.enable.basic.key;

import org.springframework.kafka.annotation.EnableKafka;

import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound.At;
import me.ehp246.test.mock.WildcardAction;

/**
 * @author Lei Yang
 *
 */
@EnableKafka
@EnableForKafka({ @Inbound(value = @At(AppConfig.TOPIC), register = WildcardAction.class) })
class AppConfig {
    final static String TOPIC = "9a2fc860-afd4-4563-a6aa-4225b465d930";
}
