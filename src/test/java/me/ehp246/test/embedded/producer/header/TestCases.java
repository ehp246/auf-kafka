package me.ehp246.test.embedded.producer.header;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfHeader;
import me.ehp246.aufkafka.api.common.AufKafkaConstant;

/**
 * @author Lei Yang
 *
 */
interface TestCases {
    @ByKafka(value = "embedded", methodAsHeader = "")
    interface Case01 {
        void header(@OfHeader Object header, @OfHeader("header02") Object value, @OfHeader("header02") Object value2);
    }

    @ByKafka(value = "embedded", headers = { "header", "${static.1}", "header2", "static.2" }, methodAsHeader = "")
    interface Case02 {
        void header();

        void header(@OfHeader Object header);
    }

    @ByKafka(value = "embedded")
    interface Case03 {
        void methodName();

        void paramHeader(@OfHeader(AufKafkaConstant.EVENT_HEADER) Object header);
    }

    @ByKafka(value = "embedded", methodAsHeader = "my.own.event")
    interface Case04 {
        void methodName();
    }
}
