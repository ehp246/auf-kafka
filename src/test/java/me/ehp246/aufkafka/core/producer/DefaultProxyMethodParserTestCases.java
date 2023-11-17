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
}