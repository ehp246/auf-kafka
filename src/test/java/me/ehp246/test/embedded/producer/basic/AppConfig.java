package me.ehp246.test.embedded.producer.basic;

import org.springframework.kafka.annotation.EnableKafka;

import me.ehp246.aufkafka.api.annotation.EnableByKafka;

/**
 * @author Lei Yang
 *
 */
@EnableKafka
@EnableByKafka
class AppConfig {
    static final String TOPIC = "558cbb7f-3fab-4b15-a9cd-8660aaaa5838";
}
