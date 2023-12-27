package me.ehp246.test.embedded.consumer.enable.basic.topic;

import org.springframework.kafka.annotation.EnableKafka;

import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound.From;
import me.ehp246.test.mock.WildcardAction;

/**
 * @author Lei Yang
 *
 */
@EnableKafka
@EnableForKafka({ @Inbound(value = @From("embedded.1"), register = WildcardAction.class),
        @Inbound(value = @From("${topic2}"), register = WildcardAction.class) })
class AppConfig {
}
