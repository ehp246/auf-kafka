package me.ehp246.test.embedded.producer.partition;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfPartition;
import me.ehp246.aufkafka.api.annotation.OfValue;

/**
 * @author Lei Yang
 *
 */
interface TestCases {
    @ByKafka(value = "embedded")
    interface Case01 {
        void newEvent(@OfValue Event event);
        
        void newEventWithPartition(@OfPartition Object partitionKey);
    }

    record Event(String id) {
    }
}
