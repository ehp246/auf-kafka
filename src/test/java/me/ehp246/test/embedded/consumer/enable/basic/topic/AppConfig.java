package me.ehp246.test.embedded.consumer.enable.basic.topic;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.annotation.EnableKafka;

import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound.From;
import me.ehp246.test.mock.WildcardAction;

/**
 * @author Lei Yang
 *
 */
@EnableConfigurationProperties({ KafkaConfig.class })
@EnableKafka
@EnableForKafka({ @Inbound(value = @From("embedded.1"), register = WildcardAction.class),
        @Inbound(value = @From("${topic2}"), register = WildcardAction.class),
        @Inbound(value = @From("#{@'kafka.config-me.ehp246.test.embedded.consumer.enable.basic.topic.KafkaConfig'.topic}"), register = WildcardAction.class) })
class AppConfig {
}
