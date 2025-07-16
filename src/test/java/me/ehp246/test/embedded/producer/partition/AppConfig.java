package me.ehp246.test.embedded.producer.partition;

import org.springframework.kafka.annotation.EnableKafka;

import me.ehp246.aufkafka.api.annotation.EnableByKafka;

/**
 * @author Lei Yang
 *
 */
@EnableKafka
@EnableByKafka
class AppConfig {
    static final String TOPIC = "8d38d61a-0236-49b1-b5f5-2818a05214bc";
}
