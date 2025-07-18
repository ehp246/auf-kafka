package me.ehp246.test.embedded.consumer.partition;

import org.springframework.kafka.annotation.EnableKafka;

import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound.From;

/**
 * @author Lei Yang
 *
 */
@EnableKafka
@EnableForKafka({ @Inbound(@From(AppConfig.TOPIC)) })
class AppConfig {
    final static String TOPIC = "b0dd30b8-7c3c-4bcc-baf0-a05749eb5373";
}
