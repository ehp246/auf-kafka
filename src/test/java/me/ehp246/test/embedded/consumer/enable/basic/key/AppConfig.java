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
@EnableForKafka({ @Inbound(value = @At("embedded"), register = WildcardAction.class) })
class AppConfig {
}
