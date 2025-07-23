package me.ehp246.test.embedded.consumer.mdc;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import me.ehp246.aufkafka.api.annotation.EnableByKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound.At;
import me.ehp246.test.mock.EmbeddedKafkaConfig;

/**
 * @author Lei Yang
 *
 */
@ComponentScan
@EnableByKafka
@EnableForKafka({ @Inbound(value = @At("embedded"),
        invocationListener = "log4jContextInvocationListener") })
@Import(EmbeddedKafkaConfig.class)
class AppConfig {
}
