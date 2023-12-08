package me.ehp246.aufkafka.core.producer;

import java.time.Instant;
import java.util.UUID;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfHeader;
import me.ehp246.aufkafka.api.annotation.OfKey;
import me.ehp246.aufkafka.api.annotation.OfPartition;
import me.ehp246.aufkafka.api.annotation.OfTimestamp;
import me.ehp246.aufkafka.api.annotation.OfTopic;
import me.ehp246.aufkafka.api.annotation.OfValue;

interface DefaultProxyMethodParserTestCases {
    @ByKafka("c26d1201-a956-4a45-a049-bc7fece18fff")
    interface TopicCase01 {
        void m01();

        void m02(@OfTopic String topic);
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

    @ByKafka("topic")
    interface PartitionCase01 {
        void m01();

        void m02(@OfPartition Object partition);
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
    }

    @ByKafka(value = "topic",
            headers = { "header1", "value1", "header2", "value2", "header1", "value2" })
    interface HeaderCase01 {
        void m01();

        void m02(@OfHeader Object header);

        void m03(@OfHeader("header1") Object value1, @OfHeader("header1") Object value2);
    }
}