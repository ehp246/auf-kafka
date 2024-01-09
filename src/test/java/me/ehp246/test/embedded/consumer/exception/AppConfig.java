package me.ehp246.test.embedded.consumer.exception;

import org.springframework.kafka.annotation.EnableKafka;

import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound.From;

/**
 * @author Lei Yang
 *
 */
@EnableKafka
@EnableForKafka({ @Inbound(value = @From("embedded")) })
class AppConfig {
}
