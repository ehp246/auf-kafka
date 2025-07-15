package me.ehp246.test.embedded.producer.header;

import org.springframework.kafka.annotation.EnableKafka;

import me.ehp246.aufkafka.api.annotation.EnableByKafka;

/**
 * @author Lei Yang
 *
 */
@EnableKafka
@EnableByKafka
class AppConfig {
    public static final String TOPIC = "c05780c5-a628-4f88-8662-e4fd913eb1d0";
}
