package me.ehp246.test.embedded.producerfn.basic;

import org.springframework.kafka.annotation.EnableKafka;

import me.ehp246.aufkafka.api.annotation.EnableByKafka;

/**
 * @author Lei Yang
 *
 */
@EnableKafka
@EnableByKafka
class AppConfig {
    public static final String TOPIC = "63a711b5-95b4-4b6f-9580-0571e339013b";
}
