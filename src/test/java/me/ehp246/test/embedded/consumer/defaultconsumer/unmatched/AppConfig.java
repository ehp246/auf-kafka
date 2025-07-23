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
@EnableForKafka(value = {
        @Inbound(value = @At("embedded"), unknownEventListener = "${default.consumer.name:}") })
class AppConfig {
    @Bean
    Unknown unknown() {
        return new Unknown();
    }
}
