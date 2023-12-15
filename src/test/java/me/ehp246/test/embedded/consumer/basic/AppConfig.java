package me.ehp246.test.embedded.consumer.basic;

import org.springframework.kafka.annotation.EnableKafka;

import me.ehp246.aufkafka.api.annotation.EnableByKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka;

/**
 * @author Lei Yang
 *
 */
@EnableKafka
@EnableByKafka
@EnableForKafka({})
class AppConfig {
}
