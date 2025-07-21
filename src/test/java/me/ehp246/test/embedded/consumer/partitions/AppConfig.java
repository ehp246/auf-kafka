package me.ehp246.test.embedded.consumer.partitions;

import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound.From;

/**
 * @author Lei Yang
 *
 */
@EnableForKafka({ @Inbound(@From(AppConfig.TOPIC)),
        @Inbound(value = @From(value = AppConfig.TOPIC, partitions = { "${prop.1}", "0", "0", "1-3", "8, 9, 10" })) })
class AppConfig {
    final static String TOPIC = "38bd28c2-9097-41a8-a58f-9b98a0d46ad6";
}
