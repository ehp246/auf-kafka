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
@EnableForKafka({ @Inbound(value = @From("42de72b9-c551-4d38-b56e-9ce0ea77e7a2")),
        @Inbound(value = @From("efec8bfb-77d8-4091-b2a7-fc9e050030b4"), eventHeader = "my.own.event.header"),
        @Inbound(value = @From("c67e2456-8427-439a-af2b-ba19eb2b7945"), eventHeader = "") })
class AppConfig {
}
