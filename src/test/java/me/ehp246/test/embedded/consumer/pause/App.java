package me.ehp246.test.embedded.consumer.pause;

import me.ehp246.aufkafka.api.annotation.EnableByKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound;
import me.ehp246.aufkafka.api.annotation.EnableForKafka.Inbound.At;

/**
 * @author Lei Yang
 *
 */
@EnableByKafka
@EnableForKafka({ @Inbound(value = @At(App.TOPIC), pollDuration = "PT0.001S") })
class App {
    final static String TOPIC = "d43e72ce-c3e5-4f26-bc20-147e804093b9";
    final static String TOPIC_2 = "fb428a09-1f26-4f0e-b6ef-ff0e9841b0ae";
    final static int MAX_POLL_INTERVAL = 50;
    final static int REPEAT = 1000;

}
