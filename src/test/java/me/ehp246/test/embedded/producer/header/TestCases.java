package me.ehp246.test.embedded.producer.header;

import java.util.UUID;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfHeader;
import me.ehp246.aufkafka.api.common.AufKafkaConstant;

/**
 * @author Lei Yang
 *
 */
interface TestCases {
    @ByKafka(value = "embedded", methodAsEvent = "")
    interface Case01 {
        void header(@OfHeader Object header, @OfHeader("header02") Object value, @OfHeader("header02") Object value2);
    }

    @ByKafka(value = "embedded", headers = { "header", "${static.1}", "header2", "static.2" }, methodAsEvent = "")
    interface Case02 {
        void header();

        void header(@OfHeader Object header);
    }

    @ByKafka(value = "embedded")
    interface Case03 {
        void methodName();

        void paramHeader(@OfHeader(AufKafkaConstant.EVENT_HEADER) Object header);
    }

    @ByKafka(value = "embedded", methodAsEvent = "my.own.event")
    interface Case04 {
        void methodName();
    }

    @ByKafka(value = "embedded")
    interface CorrelIdCase01 {
        void ping();

        void ping(@OfHeader(AufKafkaConstant.CORRELATIONID_HEADER) String correlId);

        void ping(@OfHeader("trace.id") UUID correlId);
    }
}
