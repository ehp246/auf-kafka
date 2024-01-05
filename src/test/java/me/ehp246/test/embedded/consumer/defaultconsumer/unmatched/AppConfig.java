package me.ehp246.test.embedded.consumer.defaultconsumer.unmatched;

import org.springframework.context.annotation.Bean;

import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound.From;

/**
 * @author Lei Yang
 *
 */
@EnableForKafka(value = { @Inbound(value = @From("embedded")) },
        defaultConsumer = "${default.consumer.name:}")
class AppConfig {
    @Bean
    Unmatched unmatched() {
        return new Unmatched();
    }
}
