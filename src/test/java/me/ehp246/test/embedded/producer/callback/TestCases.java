package me.ehp246.test.embedded.producer.callback;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfKey;
import me.ehp246.aufkafka.api.producer.ProducerFn.ProducerFnRecord;

/**
 * @author Lei Yang
 *
 */
interface TestCases {
    @ByKafka(value = AppConfig.TOPIC)
    interface Case01 {
        ProducerFnRecord newEvent(@OfKey String id);
    }
}
