package me.ehp246.test.embedded.producer.value.basic;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfValue;

/**
 * @author Lei Yang
 *
 */
interface TestCases {
    @ByKafka(value = "embedded")
    interface Case01 {
        void newEvent(@OfValue Event event);
    }

    record Event(String id) {
    }
}
