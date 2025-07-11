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
    @ByKafka(value = AppConfig.TOPIC, methodAsEvent = "")
    interface Case01 {
        void header(@OfHeader Object header, @OfHeader("header02") Object value, @OfHeader("header02") Object value2);
    }

    @ByKafka(value = AppConfig.TOPIC, headers = { "header", "${static.1}", "header2", "static.2" }, methodAsEvent = "")
    interface Case02 {
        void header();

        void header(@OfHeader Object header);
    }

    @ByKafka(value = AppConfig.TOPIC)
    interface Case03 {
        void methodName();

        void paramHeader(@OfHeader(AufKafkaConstant.EVENT_HEADER) Object header);
    }

    @ByKafka(value = AppConfig.TOPIC, methodAsEvent = "my.own.event")
    interface Case04 {
        void methodName();
    }

    @ByKafka(value = AppConfig.TOPIC, headers = { "h1", "hv.1", "h1", "hv.2", "H2", "h2v.1" })
    interface Case05 {
        void header(@OfHeader String h2);
    }

    @ByKafka(value = AppConfig.TOPIC)
    interface CorrelIdCase01 {
        void ping();

        void ping(@OfHeader(AufKafkaConstant.CORRELATIONID_HEADER) String correlId);

        void ping(@OfHeader("trace.id") UUID correlId);
    }
}
