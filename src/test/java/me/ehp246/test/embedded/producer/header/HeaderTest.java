package me.ehp246.test.embedded.producer.header;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.kafka.test.context.EmbeddedKafka;

import me.ehp246.aufkafka.api.common.AufKafkaConstant;
import me.ehp246.test.TestUtil;
import me.ehp246.test.mock.EmbeddedKafkaConfig;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class, MsgListener.class }, properties = {
        "static.1=234e3609-3edd-4059-b685-fa8a0bed19d3" }, webEnvironment = WebEnvironment.NONE)
@EmbeddedKafka(topics = { AppConfig.TOPIC }, partitions = 10)
class HeaderTest {
    @Autowired
    private TestCases.Case01 case01;

    @Autowired
    private TestCases.Case02 case02;

    @Autowired
    private TestCases.Case03 case03;

    @Autowired
    private TestCases.Case04 case04;
    @Autowired
    private TestCases.Case05 case05;

    @Autowired
    private TestCases.CorrelIdCase01 correlIdCase01;

    @Autowired
    private MsgListener listener;

    @Test
    void header_01() throws InterruptedException, ExecutionException {
        this.case01.header(null, null, null);

        final var headers = listener.take().headerList();

        Assertions.assertEquals("Header", headers.get(0).key());
        Assertions.assertEquals(null, headers.get(0).value());

        Assertions.assertEquals("header02", headers.get(1).key());
        Assertions.assertEquals(null, headers.get(1).value());

        Assertions.assertEquals("header02", headers.get(2).key());
        Assertions.assertEquals(null, headers.get(2).value());
    }

    @Test
    void header_02() throws InterruptedException, ExecutionException {
        final var header1 = UUID.randomUUID();
        final var header2 = UUID.randomUUID();

        this.case01.header(header1, header2, null);

        final var headers = listener.take().headerList();

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

        final var headers = listener.take().headerList();

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

        final var headers = listener.take().headerList();

        Assertions.assertEquals("header", headers.get(0).key());
        Assertions.assertEquals("234e3609-3edd-4059-b685-fa8a0bed19d3",
                new String(headers.get(0).value(), StandardCharsets.UTF_8));

        Assertions.assertEquals("header2", headers.get(1).key());
        Assertions.assertEquals("static.2", new String(headers.get(1).value(), StandardCharsets.UTF_8));

        Assertions.assertEquals("Header", headers.get(2).key());
        Assertions.assertEquals(value.toString(), new String(headers.get(2).value(), StandardCharsets.UTF_8));
    }

    @Test
    void header_05() throws InterruptedException, ExecutionException {
        final var value = UUID.randomUUID().toString();

        this.case05.header(value);

        final var headers = listener.take().headerMap();

        Assertions.assertEquals(2, headers.get("h1").size());
        Assertions.assertEquals("hv.1", headers.get("h1").get(0));
        Assertions.assertEquals("hv.2", headers.get("h1").get(1));

        Assertions.assertEquals(2, headers.get("H2").size());
        Assertions.assertEquals("h2v.1", headers.get("H2").get(0));
        Assertions.assertEquals(value, headers.get("H2").get(1), "should follow the order");
    }

    @Test
    void event_01() throws InterruptedException, ExecutionException {
        this.case03.methodName();

        final var headers = TestUtil.toList(listener.take().headers());

        Assertions.assertEquals(AufKafkaConstant.EVENT_HEADER, headers.get(0).key());
        Assertions.assertEquals("MethodName", new String(headers.get(0).value(), StandardCharsets.UTF_8));
    }

    @Test
    void event_02() throws InterruptedException, ExecutionException {
        final var expected = UUID.randomUUID().toString();
        this.case03.paramHeader(expected);

        final var headers = listener.take().headerMap();

        Assertions.assertEquals(2, headers.get(AufKafkaConstant.EVENT_HEADER).size());
        Assertions.assertEquals("ParamHeader", headers.get(AufKafkaConstant.EVENT_HEADER).get(0));
        Assertions.assertEquals(expected, headers.get(AufKafkaConstant.EVENT_HEADER).get(1));
    }

    @Test
    void event_03() throws InterruptedException, ExecutionException {
        this.case04.methodName();

        final var headers = listener.take().headerMap();

        Assertions.assertEquals(1, headers.get("my.own.event").size());
        Assertions.assertEquals("MethodName", headers.get("my.own.event").get(0));
    }

    @Test
    void correlId_01() {
        this.correlIdCase01.ping();

        final var headers = listener.take().headerMap();

        Assertions.assertEquals(1, headers.get(AufKafkaConstant.CORRELATIONID_HEADER).size());
        Assertions.assertEquals(true, !headers.get(AufKafkaConstant.CORRELATIONID_HEADER).get(0).isBlank());
    }

    @Test
    void correlId_02() {
        final var expected = UUID.randomUUID().toString();
        this.correlIdCase01.ping(expected);

        final var headers = listener.take().headerMap();

        Assertions.assertEquals(1, headers.get(AufKafkaConstant.CORRELATIONID_HEADER).size());
        Assertions.assertEquals(expected, headers.get(AufKafkaConstant.CORRELATIONID_HEADER).get(0));
    }
}
