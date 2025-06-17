package me.ehp246.test.embedded.producerfn.value;

import org.springframework.kafka.annotation.EnableKafka;

import me.ehp246.aufkafka.api.annotation.EnableByKafka;

/**
 * @author Lei Yang
 *
 */
@EnableKafka
@EnableByKafka
class AppConfig {
    public static final String TOPIC = "638cb5bd-7935-4781-94df-53f608c70088";
}
