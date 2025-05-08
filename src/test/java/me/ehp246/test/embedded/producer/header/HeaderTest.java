package me.ehp246.test.embedded.producer.header;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

import me.ehp246.aufkafka.api.AufKafkaConstant;
import me.ehp246.aufkafka.core.util.OneUtil;
import me.ehp246.test.mock.EmbeddedKafkaConfig;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class, MsgListener.class }, properties = {
        "static.1=234e3609-3edd-4059-b685-fa8a0bed19d3" })
@EmbeddedKafka(topics = { "embedded" }, partitions = 10)
class HeaderTest {
    @Autowired
    private TestCases.Case01 case01;

    @Autowired
    private TestCases.Case02 case02;

    @Autowired
    private MsgListener listener;

    @BeforeEach
    void reset() {
        listener.reset();
    }

    @Test
    void header_01() throws InterruptedException, ExecutionException {
        this.case01.header(null, null, null);

        final var headers = OneUtil.toList(listener.take().headers());

        Assertions.assertEquals(4, headers.size());

        Assertions.assertEquals("Header", headers.get(0).key());
        Assertions.assertEquals(null, headers.get(0).value());

        Assertions.assertEquals("header02", headers.get(1).key());
        Assertions.assertEquals(null, headers.get(1).value());

        Assertions.assertEquals("header02", headers.get(2).key());
        Assertions.assertEquals(null, headers.get(2).value());

        Assertions.assertEquals(AufKafkaConstant.HEADER_NAME_EVENT_TYPE, headers.get(3).key());
        Assertions.assertEquals("Header", new String(headers.get(3).value(), StandardCharsets.UTF_8));
    }

    @Test
    void header_02() throws InterruptedException, ExecutionException {
        final var header1 = UUID.randomUUID();
        final var header2 = UUID.randomUUID();

        this.case01.header(header1, header2, null);

        final var headers = OneUtil.toList(listener.take().headers());

        Assertions.assertEquals(4, headers.size());

        Assertions.assertEquals("Header", headers.get(0).key());
        Assertions.assertEquals(true,
                new String(headers.get(0).value(), StandardCharsets.UTF_8).equals(header1.toString()));

        Assertions.assertEquals("header02", headers.get(1).key());
        Assertions.assertEquals(true,
                new String(headers.get(1).value(), StandardCharsets.UTF_8).equals(header2.toString()));

        Assertions.assertEquals("header02", headers.get(2).key());
        Assertions.assertEquals(null, headers.get(2).value());
    }

    @Test
    void header_03() throws InterruptedException, ExecutionException {
        this.case02.header();

        final var headers = OneUtil.toList(listener.take().headers());

        Assertions.assertEquals(3, headers.size());

        Assertions.assertEquals("header", headers.get(0).key());
        Assertions.assertEquals(true, new String(headers.get(0).value(), StandardCharsets.UTF_8)
                .equals("234e3609-3edd-4059-b685-fa8a0bed19d3"));

        Assertions.assertEquals("header2", headers.get(1).key());
        Assertions.assertEquals(true, new String(headers.get(1).value(), StandardCharsets.UTF_8).equals("static.2"));
    }

    @Test
    void header_04() throws InterruptedException, ExecutionException {
        final var value = UUID.randomUUID();

        this.case02.header(value);

        final var headers = OneUtil.toList(listener.take().headers());

        Assertions.assertEquals(4, headers.size());

        Assertions.assertEquals("Header", headers.get(0).key());
        Assertions.assertEquals(true,
                new String(headers.get(0).value(), StandardCharsets.UTF_8).equals(value.toString()));

        Assertions.assertEquals("header", headers.get(1).key());
        Assertions.assertEquals(true, new String(headers.get(1).value(), StandardCharsets.UTF_8)
                .equals("234e3609-3edd-4059-b685-fa8a0bed19d3"));

        Assertions.assertEquals("header2", headers.get(2).key());
        Assertions.assertEquals(true, new String(headers.get(2).value(), StandardCharsets.UTF_8).equals("static.2"));
    }
}
