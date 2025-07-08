package me.ehp246.test.embedded.producer.basic;

import java.time.Instant;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfHeader;
import me.ehp246.aufkafka.api.annotation.OfKey;
import me.ehp246.aufkafka.api.annotation.OfPartition;
import me.ehp246.aufkafka.api.annotation.OfTimestamp;
import me.ehp246.aufkafka.api.common.AufKafkaConstant;
import me.ehp246.aufkafka.api.producer.ProducerSendRecord;

/**
 * @author Lei Yang
 *
 */
interface TestCases {
    @ByKafka(value = "embedded")
    interface Case01 {
        void newEvent();

        ProducerSendRecord newSendEvent(@OfKey String id);

        void newEventType(@OfHeader(AufKafkaConstant.EVENT_HEADER) String eventType);

        void newEvent(@OfKey String companyId);

        void newEvent(@OfTimestamp Instant timestamp);

        void newEvent(@OfTimestamp Long timestamp);

        void newEventWithPartition(@OfPartition Object partitionKey);
    }
}
