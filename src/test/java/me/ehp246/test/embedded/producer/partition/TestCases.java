package me.ehp246.test.embedded.producer.partition;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfPartition;
import me.ehp246.aufkafka.api.annotation.OfValue;
import me.ehp246.aufkafka.api.producer.DirectPartitionMap;

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

    @ByKafka(value = "embedded", partitionMap = DirectPartitionMap.class)
    interface Case02 {
        void newEvent(@OfPartition Event event);

        void newEventWithDirectPartition(@OfPartition Integer partition);

        void newEventWithDirectPartition(@OfPartition int partition);
    }

    record Event(String id) {
    }
}
