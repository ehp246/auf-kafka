package me.ehp246.aufkafka.core.producer;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfHeader;
import me.ehp246.aufkafka.api.annotation.OfKey;
import me.ehp246.aufkafka.api.annotation.OfPartition;
import me.ehp246.aufkafka.api.annotation.OfTimestamp;
import me.ehp246.aufkafka.api.annotation.OfTopic;
import me.ehp246.aufkafka.api.annotation.OfValue;
import me.ehp246.aufkafka.api.common.AufKafkaConstant;
import me.ehp246.aufkafka.api.producer.OutboundEvent;
import me.ehp246.aufkafka.api.producer.ProducerFn.ProducerFnRecord;

interface DefaultProxyMethodParserTestCases {
    @ByKafka("c26d1201-a956-4a45-a049-bc7fece18fff")
    interface TopicCase01 {
        void m01();

        void m02(@OfTopic String topic);

        @OfTopic("22c33a9d-24ec-438c-a8d7-d4821fde0bea")
        void m03(@OfTopic String topic);

        @OfTopic("${method.topic.name}")
        void m03();
    }

    @ByKafka("${topic.name}")
    interface TopicCase02 {
        void m01();
    }

    @ByKafka("topic")
    interface KeyCase01 {
        void m01();

        @OfKey("aa143627-0e3f-4758-a7cf-e56db55c77c1")
        void m02(@OfKey Object key);

        @OfKey
        void m03();

        @OfKey("887114e5-5770-4f7f-b0c6-e0803753eb58")
        void m04();
    }

    @ByKafka(value = "", key = "244c50cd-3624-4194-9546-86c486281b4f")
    interface KeyCase02 {
        void m01();

        @OfKey
        void m03();

        @OfKey("887114e5-5770-4f7f-b0c6-e0803753eb58")
        void m04();
    }

    @ByKafka(value = "", key = "${key.type}")
    interface KeyCase03 {
        void m01();

        @OfKey("${key.m03}")
        void m03();
    }

    @ByKafka("topic")
    interface EventTypeCase01 {
        void m01();

        void m02(@OfHeader(AufKafkaConstant.EVENT_HEADER) Object key);
    }

    @ByKafka("topic")
    interface PartitionCase01 {
        void m01();

        void m02(@OfPartition Integer partition);

        void m03(@OfPartition int partition);

        /**
         * Unsupported
         */
        void m04(@OfPartition String partition);

        @OfPartition
        void onMethod01();

        @OfPartition(6)
        void onMethod02();
    }

    @ByKafka(value = "", partition = 2)
    interface PartitionCase02 {
        void onType();

        @OfPartition(3)
        void onParam(@OfPartition int partition);

        @OfPartition
        void onMethod01();

        @OfPartition(6)
        void onMethod02();

        @OfPartition(4)
        void onParam(@OfPartition Integer partition);
    }

    @ByKafka("topic")
    interface TimestampCase01 {
        void m01();

        void m02(@OfTimestamp Instant timestamp);

        void m03(@OfTimestamp Long timestamp);

        void m04(@OfTimestamp long timestamp);
    }

    @ByKafka("topic")
    interface ValueCase01 {
        void m01();

        void m02(@OfValue Instant value);

        void m03(UUID uuid);

        void m04(UUID uuid, @OfValue UUID value);
    }

    @ByKafka(value = "topic", header = { "header1", "value1", "header2", "value2", "header1",
            "value2" }, methodAsEvent = "")
    interface HeaderCase01 {
        void m01();

        void m02(@OfHeader UUID header);
    }

    @ByKafka(value = "topic", methodAsEvent = "")
    interface HeaderCase02 {
        void m01();

        void m03(@OfHeader("header1") Object value1, @OfHeader("header1") Object value2);
    }

    @ByKafka(value = "topic", methodAsEvent = "", header = { "header1", "${value1}", "header2", "value2" })
    interface HeaderCase03 {
        void m01();
    }

    @ByKafka("")
    interface ReturnCase01 {
        void m01();

        Void m02();

        @SuppressWarnings("rawtypes")
        CompletableFuture m03();

        CompletableFuture<RecordMetadata> m05();

        ProducerRecord<String, String> m06();

        RecordMetadata m07();

        OutboundEvent m08();

        ProducerFnRecord m09();
    }
}