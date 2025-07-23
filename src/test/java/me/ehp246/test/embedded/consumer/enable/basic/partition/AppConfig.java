package me.ehp246.test.embedded.consumer.enable.basic.partition;

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
@EnableForKafka({
        @Inbound(value = @At(value = AppConfig.TOPIC, partitions = { "${p.3}" }), register = WildcardAction.class) })
class AppConfig {
    final static String TOPIC = "aaa7b9d5-48ad-4542-8290-77c222fc146f";
}
