package me.ehp246.test.embedded.producer.partition;

import org.apache.kafka.clients.producer.ProducerRecord;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfKey;
import me.ehp246.aufkafka.api.annotation.OfPartition;

/**
 * @author Lei Yang
 *
 */
interface TestCases {
    @ByKafka(value = AppConfig.TOPIC)
    interface Case01 {
        void onParam(@OfPartition int partition);

        @OfPartition
        ProducerRecord<String, String> onMethod01();

        @OfPartition(6)
        ProducerRecord<String, String> onMethod02();
    }

    @ByKafka(value = AppConfig.TOPIC, partition = 2)
    interface Case02 {
        void onType();

        ProducerRecord<String, String> onParam(@OfPartition Integer partition);

        @OfPartition(7)
        ProducerRecord<String, String> onParam02(@OfPartition Integer partition);

        @OfPartition
        ProducerRecord<String, String> onMethod01();

        @OfPartition(6)
        ProducerRecord<String, String> onMethod02(@OfKey String key);
    }
}
