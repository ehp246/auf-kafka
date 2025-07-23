package me.ehp246.test.embedded.consumer.enable.basic.topic;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.annotation.EnableKafka;

import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound.At;
import me.ehp246.test.mock.WildcardAction;

/**
 * @author Lei Yang
 *
 */
@EnableConfigurationProperties({ KafkaConfig.class })
@EnableKafka
@EnableForKafka({ @Inbound(value = @At(AppConfig.TOPIC + ".1"), register = WildcardAction.class),
        @Inbound(value = @At("${topic2}"), register = WildcardAction.class),
        @Inbound(value = @At("#{@'kafka.config-me.ehp246.test.embedded.consumer.enable.basic.topic.KafkaConfig'.topic}"), register = WildcardAction.class) })
class AppConfig {
    final static String TOPIC = "7d9052da-86e0-4851-aac9-9e59cce05f05";
}
