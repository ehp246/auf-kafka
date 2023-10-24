package me.ehp246.test.embedded.producer.basic;

import me.ehp246.aufkafka.api.annotation.ByProducer;

/**
 * @author Lei Yang
 *
 */
interface TestCases {
    @ByProducer(value = "basic")
    interface Case01 {
        void newEvent(Event event);
    }

    record Event(String id) {
    }
}
