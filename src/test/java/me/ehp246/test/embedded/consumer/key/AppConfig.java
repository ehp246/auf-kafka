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
@EnableForKafka({ @Inbound(value = @At(AppConfig.TOPIC)) })
class AppConfig {
    final static String TOPIC = "32a96fdb-9840-42f2-b890-7e3e519fdec7";
}
