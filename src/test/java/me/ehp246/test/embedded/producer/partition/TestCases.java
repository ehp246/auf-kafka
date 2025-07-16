package me.ehp246.test.embedded.producer.partition;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfPartition;
import me.ehp246.aufkafka.api.producer.ProducerFn.ProducerFnRecord;

/**
 * @author Lei Yang
 *
 */
interface TestCases {
    @ByKafka(value = AppConfig.TOPIC)
    interface Case01 {
        void onParam(@OfPartition int partition);

        @OfPartition
        ProducerFnRecord onMethod01();

        @OfPartition(6)
        void onMethod02();
    }

    @ByKafka(value = AppConfig.TOPIC)
    @OfPartition(2)
    interface Case02 {
        void onType();

        void onParam(@OfPartition int partition);

        @OfPartition
        void onMethod01();

        @OfPartition(6)
        void onMethod02();
    }
}
