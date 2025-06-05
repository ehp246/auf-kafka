package me.ehp246.test.embedded.consumer.pause;

import me.ehp246.aufkafka.api.annotation.EnableByKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound.From;

/**
 * @author Lei Yang
 *
 */
@EnableByKafka
@EnableForKafka({ @Inbound(value = @From(App.TOPIC), pollDuration = "PT0.1S") })
class App {
    final static String TOPIC = "d43e72ce-c3e5-4f26-bc20-147e804093b9";
}
