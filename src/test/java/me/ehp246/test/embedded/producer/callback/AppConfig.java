package me.ehp246.test.embedded.producer.callback;

import org.springframework.kafka.annotation.EnableKafka;

import me.ehp246.aufkafka.api.annotation.EnableByKafka;

/**
 * @author Lei Yang
 *
 */
@EnableKafka
@EnableByKafka
class AppConfig {
    public static final String TOPIC = "c56ce293-a979-4712-bfcc-16b7f833522b";
}
