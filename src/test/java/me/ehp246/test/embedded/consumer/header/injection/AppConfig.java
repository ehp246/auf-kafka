package me.ehp246.test.embedded.consumer.header.injection;

import org.springframework.kafka.annotation.EnableKafka;

import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound.At;

/**
 * @author Lei Yang
 *
 */
@EnableKafka
@EnableForKafka({ @Inbound(value = @At(AppConfig.TOPIC)) })
class AppConfig {
    public final static String TOPIC = "b4145f3a-3cb5-4a0b-9265-b540c641fe1b";
}
