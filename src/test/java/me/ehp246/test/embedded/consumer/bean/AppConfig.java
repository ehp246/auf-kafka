package me.ehp246.test.embedded.consumer.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.annotation.EnableKafka;

import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound.From;

/**
 * @author Lei Yang
 *
 */
@EnableKafka
@EnableForKafka({ @Inbound(@From("embedded")), @Inbound(value = @From("embedded"),pollDuration = "PT1S") })
@EnableConfigurationProperties({ AppConfig.KafkaConfig.class })
class AppConfig {
    @ConfigurationProperties(prefix = "kafka.config")
    static record KafkaConfig(String topic) {
    }
}
