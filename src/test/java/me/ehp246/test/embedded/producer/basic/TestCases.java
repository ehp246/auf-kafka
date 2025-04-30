package me.ehp246.test.embedded.producer.basic;

import java.time.Instant;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfEventType;
import me.ehp246.aufkafka.api.annotation.OfKey;
import me.ehp246.aufkafka.api.annotation.OfPartition;
import me.ehp246.aufkafka.api.annotation.OfTimestamp;

/**
 * @author Lei Yang
 *
 */
interface TestCases {
    @ByKafka(value = "embedded")
    interface Case01 {
        void newEvent();

        @OfEventType("bc130e00-97fa-475a-b36d-2cd99389b915")
        void newEventType();

        void newEventType(@OfEventType String eventType);

        void newEvent(@OfKey String companyId);

        void newEvent(@OfTimestamp Instant timestamp);

        void newEvent(@OfTimestamp Long timestamp);

        void newEventWithPartition(@OfPartition Object partitionKey);
    }
}
