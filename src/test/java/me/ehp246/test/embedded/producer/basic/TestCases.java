package me.ehp246.test.embedded.producer.basic;

import me.ehp246.aufkafka.api.annotation.ByKafka;

/**
 * @author Lei Yang
 *
 */
interface TestCases {
    @ByKafka(value = "basic")
    interface Case01 {
        void newEvent(Event event);
    }

    record Event(String id) {
    }
}
