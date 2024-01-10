package me.ehp246.test.embedded.consumer.listener.completed;

import org.springframework.context.annotation.ComponentScan;

import me.ehp246.aufkafka.api.annotation.EnableByKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound.From;

/**
 * @author Lei Yang
 *
 */
@ComponentScan
@EnableByKafka
@EnableForKafka({ @Inbound(value = @From("embedded"), invocationListener = "${comp1.name:}") })
class AppConfig {
}
