package me.ehp246.test.embedded.consumer.listener.completed;

import org.springframework.context.annotation.ComponentScan;

import me.ehp246.aufkafka.api.annotation.EnableByKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound.At;

/**
 * @author Lei Yang
 *
 */
@ComponentScan
@EnableByKafka
@EnableForKafka({ @Inbound(value = @At(AppConfig.TOPIC), invocationListener = "${comp1.name:}") })
class AppConfig {
    final static String TOPIC = "dd0ed70b-8e96-4e53-bd56-1834d4bd0a4b";
}
