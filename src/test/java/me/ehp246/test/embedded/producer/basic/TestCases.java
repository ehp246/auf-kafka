package me.ehp246.test.embedded.producer.basic;

import java.time.Instant;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfTimestamp;
import me.ehp246.aufkafka.api.annotation.OfValue;

/**
 * @author Lei Yang
 *
 */
interface TestCases {
    @ByKafka(value = "embedded")
    interface Case01 {
        void newEvent(@OfValue Event event);
        void newEvent(@OfValue Event event, @OfTimestamp Instant timestamp);
        void newEvent(@OfValue Event event, @OfTimestamp Long timestamp);
    }

    record Event(String id) {
    }
}
