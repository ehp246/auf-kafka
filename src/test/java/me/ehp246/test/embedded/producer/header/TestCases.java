package me.ehp246.test.embedded.producer.header;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfHeader;
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
        void header(@OfHeader Object header, @OfHeader("header02") Object value, @OfHeader("header02") Object value2);
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
