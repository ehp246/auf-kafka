package me.ehp246.test.embedded.consumer.defaultconsumer.unmatched;

import org.springframework.context.annotation.Bean;

import me.ehp246.aufkafka.api.annotation.EnableByKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound.At;

/**
 * @author Lei Yang
 *
 */
@EnableByKafka
@EnableForKafka(value = { @Inbound(value = @At(AppConfig.TOPIC), unknownEventListener = "${default.consumer.name:}") })
class AppConfig {
    final static String TOPIC = "8449e0fd-7e14-409d-b423-d4094f41e5b0";

    @Bean
    Unknown unknown() {
        return new Unknown();
    }
}
