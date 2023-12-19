package me.ehp246.test.embedded.consumer.topic;

import me.ehp246.aufkafka.api.annotation.ByKafka;

/**
 * @author Lei Yang
 *
 */
interface TestCases {
    @ByKafka(value = "embedded")
    interface Case01 {
        void newEvent();
    }
}
