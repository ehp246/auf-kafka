package me.ehp246.test.embedded.consumer.header.event;

import org.springframework.kafka.annotation.EnableKafka;

import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound.From;

/**
 * @author Lei Yang
 *
 */
@EnableKafka
@EnableForKafka({ @Inbound(value = @From("topic1")),
        @Inbound(value = @From("topic2"), eventHeader = "my.own.event.header"),
        @Inbound(value = @From("topic3"), eventHeader = "") })
class AppConfig {
}
