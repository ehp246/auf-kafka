package me.ehp246.test.embedded.consumer.key;

import org.springframework.kafka.annotation.EnableKafka;

import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound.At;

/**
 * @author Lei Yang
 *
 */
@EnableKafka
@EnableForKafka({ @Inbound(value = @At("embedded")) })
class AppConfig {
}
