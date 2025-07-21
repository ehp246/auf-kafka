package me.ehp246.test.embedded.consumer.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound.From;

/**
 * @author Lei Yang
 *
 */
@EnableForKafka({ @Inbound(@From(AppConfig.TOPIC)), @Inbound(value = @From(AppConfig.TOPIC), pollDuration = "PT1S") })
@EnableConfigurationProperties({ AppConfig.KafkaConfig.class })
class AppConfig {
    final static String TOPIC = "e895345c-e748-4309-a19f-883fc07592b5";

    @ConfigurationProperties(prefix = "kafka.config")
    static record KafkaConfig(String topic) {
    }
}
