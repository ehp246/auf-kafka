package me.ehp246.test.embedded.producer.basic;

import java.time.Instant;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfHeader;
import me.ehp246.aufkafka.api.annotation.OfKey;
import me.ehp246.aufkafka.api.annotation.OfTimestamp;
import me.ehp246.aufkafka.api.common.AufKafkaConstant;
import me.ehp246.aufkafka.api.producer.ProducerFn.ProducerFnRecord;

/**
 * @author Lei Yang
 *
 */
interface TestCases {
    @ByKafka(value = AppConfig.TOPIC)
    interface Case01 {
        void newEvent();

        ProducerFnRecord newSendEvent(@OfKey String key);

        void newEventType(@OfHeader(AufKafkaConstant.EVENT_HEADER) String eventType);

        void newEvent(@OfKey String companyId);

        void newEvent(@OfTimestamp Instant timestamp);

        void newEvent(@OfTimestamp Long timestamp);
    }
}
