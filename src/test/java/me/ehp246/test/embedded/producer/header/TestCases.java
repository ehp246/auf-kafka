package me.ehp246.test.embedded.producer.header;

import me.ehp246.aufkafka.api.AufKafkaConstant;
import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfHeader;

/**
 * @author Lei Yang
 *
 */
interface TestCases {
    @ByKafka(value = "embedded", eventHeader = "")
    interface Case01 {
        void header(@OfHeader Object header, @OfHeader("header02") Object value, @OfHeader("header02") Object value2);
    }

    @ByKafka(value = "embedded", headers = { "header", "${static.1}", "header2", "static.2" }, eventHeader = "")
    interface Case02 {
        void header();

        void header(@OfHeader Object header);
    }

    @ByKafka(value = "embedded")
    interface Case03 {
        void methodName();

        void paramHeader(@OfHeader(AufKafkaConstant.EVENT_HEADER) Object header);
    }

    @ByKafka(value = "embedded", eventHeader = "my.own.event")
    interface Case04 {
        void methodName();
    }
}
