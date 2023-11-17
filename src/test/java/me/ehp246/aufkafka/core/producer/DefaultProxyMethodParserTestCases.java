package me.ehp246.aufkafka.core.producer;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfKey;
import me.ehp246.aufkafka.api.annotation.OfPartition;
import me.ehp246.aufkafka.api.annotation.OfTopic;

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

    interface KeyCase {
        void m01();

        void m02(@OfKey String key);
    }

    void m03(@OfPartition Integer partition);

    void m04(@OfPartition int partiion);
}