package me.ehp246.aufkafka.core.producer;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.Uuid;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufkafka.api.common.AufKafkaConstant;
import me.ehp246.aufkafka.api.producer.OutboundEvent;
import me.ehp246.aufkafka.api.producer.ProducerFn.ProducerFnRecord;
import me.ehp246.test.TestUtil;

/**
 * @author Lei Yang
 *
 */
class DefaultProxyMethodParserTest {
    private final ProxyMethodParser parser = new DefaultProxyMethodParser(new MockEnvironment()
            .withProperty("topic.name", "bc5beb1b-569c-4055-bedf-3b06f9af2e5d")::resolvePlaceholders);

    @Test
    void topic_01() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.TopicCase01.class);

        captor.proxy().m01();

        final var event = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args());

        Assertions.assertEquals("c26d1201-a956-4a45-a049-bc7fece18fff", event.topic());
    }

    @Test
    void topic_02() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.TopicCase01.class);

        captor.proxy().m02(null);

        final var event = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args());

        Assertions.assertEquals(null, event.topic());
    }

    @Test
    void topic_03() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.TopicCase01.class);
        final var expected = Uuid.randomUuid().toString();

        captor.proxy().m02(expected);

        final var event = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args());

        Assertions.assertEquals(expected, event.topic());
    }

    @Test
    void topic_04() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.TopicCase02.class);

        captor.proxy().m01();

        final var event = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args());

        Assertions.assertEquals("bc5beb1b-569c-4055-bedf-3b06f9af2e5d", event.topic());
    }

    @Test
    void key_01() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.KeyCase01.class);

        captor.proxy().m01();

        final var event = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args());

        Assertions.assertEquals(null, event.key());
    }

    @Test
    void key_02() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.KeyCase01.class);
        final var expected = UUID.randomUUID();

        captor.proxy().m02(expected);

        final var event = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args());

        Assertions.assertEquals(expected.toString(), event.key());
    }

    @Test
    void key_03() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.KeyCase01.class);

        captor.proxy().m02(null);

        final var event = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args());

        Assertions.assertEquals(null, event.key());
    }

    @Test
    void key_04() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.KeyCase01.class);

        captor.proxy().m03();

        final var event = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args());

        Assertions.assertEquals(null, event.key(), "should supress");
    }

    @Test
    void key_05() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.KeyCase01.class);

        captor.proxy().m04();

        final var event = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args());

        Assertions.assertEquals("887114e5-5770-4f7f-b0c6-e0803753eb58", event.key(), "should follow annotation");
    }

    @Test
    void event_01() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.EventTypeCase01.class);

        captor.proxy().m01();

        final var event = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args());

        Assertions.assertEquals("M01", TestUtil.getLastValue(event.headers(), AufKafkaConstant.EVENT_HEADER));
    }

    @Test
    void event_02() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.EventTypeCase01.class);
        final var expected = UUID.randomUUID();

        captor.proxy().m02(expected);

        final var event = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args());

        Assertions.assertEquals(expected.toString(),
                TestUtil.getLastValue(event.headers(), AufKafkaConstant.EVENT_HEADER));
    }

    @Test
    void event_03() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.EventTypeCase01.class);

        captor.proxy().m02(null);

        final var event = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args());

        Assertions.assertEquals(null, TestUtil.getLastValue(event.headers(), AufKafkaConstant.EVENT_HEADER));
    }

    @Test
    void partition_01() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.PartitionCase01.class);

        captor.proxy().m01();

        final var event = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args());

        Assertions.assertEquals(null, event.partition());
    }

    @Test
    void partition_02() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.PartitionCase01.class);
        final var expected = Integer.valueOf((int) (Math.random() * 100));

        captor.proxy().m02(expected);

        final var event = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args());

        Assertions.assertEquals(true, event.partition() == expected);
    }

    @Test
    void partition_03() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.PartitionCase01.class);

        captor.proxy().m02(null);

        final var event = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args());

        Assertions.assertEquals(null, event.partition());
    }

    @Test
    void partition_04() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.PartitionCase01.class);

        captor.proxy().m03(-1);

        final var event = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args());

        Assertions.assertEquals(-1, event.partition());
    }

    @Test
    void partition_05() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.PartitionCase01.class);

        captor.proxy().m04(null);

        Assertions.assertThrows(UnsupportedOperationException.class, () -> parser.parse(captor.invocation().method()));
    }

    @Test
    void timestamp_01() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.TimestampCase01.class);

        captor.proxy().m01();

        final var event = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args());

        Assertions.assertEquals(null, event.timestamp());
    }

    @Test
    void timestamp_02() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.TimestampCase01.class);
        final var expected = Instant.now();

        captor.proxy().m02(expected);

        final var event = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args());

        Assertions.assertEquals(expected, event.timestamp());
    }

    @Test
    void timestamp_03() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.TimestampCase01.class);

        captor.proxy().m02(null);

        final var event = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args());

        Assertions.assertEquals(null, event.timestamp());
    }

    @Test
    void timestamp_04() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.TimestampCase01.class);
        final var expected = Instant.now().toEpochMilli();

        captor.proxy().m03(expected);

        final var event = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args());

        Assertions.assertEquals(expected, event.timestamp().toEpochMilli());
    }

    @Test
    void timestamp_05() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.TimestampCase01.class);

        captor.proxy().m03(null);

        final var event = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args());

        Assertions.assertEquals(null, event.timestamp());
    }

    @Test
    void timestamp_06() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.TimestampCase01.class);
        final var expected = Instant.now();
        captor.proxy().m04(expected.toEpochMilli());

        final var event = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args());

        Assertions.assertEquals(expected.toEpochMilli(), event.timestamp().toEpochMilli());
    }

    @Test
    void value_01() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.ValueCase01.class);

        captor.proxy().m01();

        final var event = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args());

        Assertions.assertEquals(null, event.value());
    }

    @Test
    void value_02() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.ValueCase01.class);
        final var expected = Instant.now();

        captor.proxy().m02(expected);

        final var event = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args());

        Assertions.assertEquals(expected, event.value());
    }

    @Test
    void value_03() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.ValueCase01.class);

        captor.proxy().m02(null);

        final var event = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args());

        Assertions.assertEquals(null, event.value());
    }

    @Test
    void value_04() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.ValueCase01.class);

        captor.proxy().m03(UUID.randomUUID());

        final var event = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args());

        Assertions.assertEquals(null, event.value(), "should ignore un-annotated");
    }

    @Test
    void value_05() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.ValueCase01.class);

        captor.proxy().m04(UUID.randomUUID(), UUID.randomUUID());

        final var event = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args());

        Assertions.assertEquals(captor.invocation().args()[1], event.value(), "should ignore un-annotated");
    }

    @Test
    void header_01() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.HeaderCase01.class);

        captor.proxy().m01();

        final var headers = TestUtil.toList(parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args()).headers());

        Assertions.assertEquals(3, headers.size());
        Assertions.assertEquals("header1", headers.get(0).key());
        Assertions.assertEquals("value1", headers.get(0).value());

        Assertions.assertEquals("header2", headers.get(1).key());
        Assertions.assertEquals("value2", headers.get(1).value());

        Assertions.assertEquals("header1", headers.get(2).key());
        Assertions.assertEquals("value2", headers.get(2).value());
    }

    @Test
    void header_02() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.HeaderCase01.class);

        captor.proxy().m02(UUID.randomUUID());

        final var headers = TestUtil.toList(parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args()).headers());

        Assertions.assertEquals(4, headers.size());

        Assertions.assertEquals("Header", headers.get(3).key());
        Assertions.assertEquals(captor.invocation().args()[0], headers.get(3).value());

        Assertions.assertEquals("header1", headers.get(2).key());
        Assertions.assertEquals("value2", headers.get(2).value());

        Assertions.assertEquals("header2", headers.get(1).key());
        Assertions.assertEquals("value2", headers.get(1).value());

        Assertions.assertEquals("header1", headers.get(0).key());
        Assertions.assertEquals("value1", headers.get(0).value());
    }

    @Test
    void header_03() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.HeaderCase01.class);

        captor.proxy().m02(null);

        final var headers = TestUtil.toList(parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args()).headers());

        Assertions.assertEquals(4, headers.size());

        Assertions.assertEquals("Header", headers.get(3).key());
        Assertions.assertEquals(captor.invocation().args()[0], headers.get(3).value());
    }

    @Test
    void header_05() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.HeaderCase02.class);
        captor.proxy().m01();

        final var headers = TestUtil.toList(parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args()).headers());

        Assertions.assertEquals(0, headers.size());
    }

    @Test
    void header_06() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.HeaderCase02.class);
        captor.proxy().m03(UUID.randomUUID(), UUID.randomUUID());

        final var headers = TestUtil.toList(parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args()).headers());

        Assertions.assertEquals(2, headers.size());

        Assertions.assertEquals("header1", headers.get(0).key());
        Assertions.assertEquals(captor.invocation().args()[0], headers.get(0).value());

        Assertions.assertEquals("header1", headers.get(1).key());
        Assertions.assertEquals(captor.invocation().args()[1], headers.get(1).value());
    }

    @Test
    void header_07() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.HeaderCase02.class);
        captor.proxy().m03(null, UUID.randomUUID());

        final var headers = TestUtil.toList(parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args()).headers());

        Assertions.assertEquals(2, headers.size());

        Assertions.assertEquals("header1", headers.get(0).key());
        Assertions.assertEquals(captor.invocation().args()[0], headers.get(0).value());

        Assertions.assertEquals("header1", headers.get(1).key());
        Assertions.assertEquals(captor.invocation().args()[1], headers.get(1).value());
    }

    @Test
    void header_08() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.HeaderCase03.class);

        captor.proxy().m01();

        final var headers = TestUtil.toList(new DefaultProxyMethodParser(new MockEnvironment().withProperty("value1",
                "bc5beb1b-569c-4055-bedf-3b06f9af2e5d")::resolvePlaceholders).parse(captor.invocation().method())
                .invocationBinder().apply(captor.invocation().target(), captor.invocation().args()).headers());

        Assertions.assertEquals(2, headers.size());

        Assertions.assertEquals("header1", headers.get(0).key());
        Assertions.assertEquals("bc5beb1b-569c-4055-bedf-3b06f9af2e5d", headers.get(0).value());

        Assertions.assertEquals("header2", headers.get(1).key());
        Assertions.assertEquals("value2", headers.get(1).value());
    }

    @Test
    void return_01() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.ReturnCase01.class);

        captor.proxy().m01();

        final var binder = parser.parse(captor.invocation().method()).returnBinder();

        Assertions.assertEquals(true, binder instanceof LocalReturnBinder);

        Assertions.assertEquals(null, ((LocalReturnBinder) binder).apply(null,
                new ProducerFnRecord(null, new CompletableFuture<RecordMetadata>())));
    }

    @Test
    void return_02() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.ReturnCase01.class);

        captor.proxy().m02();

        final var binder = parser.parse(captor.invocation().method()).returnBinder();

        Assertions.assertEquals(true, binder instanceof LocalReturnBinder);

        Assertions.assertEquals(null, ((LocalReturnBinder) binder).apply(null,
                new ProducerFnRecord(null, new CompletableFuture<RecordMetadata>())));
    }

    @Test
    void return_03() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.ReturnCase01.class);

        captor.proxy().m03();

        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> parser.parse(captor.invocation().method()).returnBinder());
    }

    @Test
    void return_04() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.ReturnCase01.class);

        captor.proxy().m07();

        final var binder = (LocalReturnBinder) parser.parse(captor.invocation().method()).returnBinder();

        final var sentFuture = new CompletableFuture<RecordMetadata>();
        sentFuture.complete(Mockito.mock(RecordMetadata.class));

        Assertions.assertEquals(sentFuture.get(), binder.apply(null, new ProducerFnRecord(null, sentFuture)));
    }

    @Test
    void return_05() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.ReturnCase01.class);

        captor.proxy().m09();

        final var binder = (LocalReturnBinder) parser.parse(captor.invocation().method()).returnBinder();

        final var expected = new ProducerFnRecord(null, new CompletableFuture<RecordMetadata>());

        Assertions.assertEquals(expected, binder.apply(null, expected));
    }

    @Test
    void return_06() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.ReturnCase01.class);

        captor.proxy().m05();

        final var binder = (LocalReturnBinder) parser.parse(captor.invocation().method()).returnBinder();

        final var sentFuture = new CompletableFuture<RecordMetadata>();

        final var returned = binder.apply(null, new ProducerFnRecord(null, sentFuture));

        Assertions.assertEquals(sentFuture, returned);
    }

    @Test
    void return_07() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.ReturnCase01.class);

        captor.proxy().m08();

        final var binder = (LocalReturnBinder) parser.parse(captor.invocation().method()).returnBinder();

        final var sentFuture = new CompletableFuture<RecordMetadata>();
        final var expected = Mockito.mock(OutboundEvent.class);
        sentFuture.complete(null);

        final var returned = binder.apply(expected, new ProducerFnRecord(null, sentFuture));

        Assertions.assertEquals(expected, returned);
    }

    @SuppressWarnings("unchecked")
    @Test
    void return_08() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.ReturnCase01.class);

        captor.proxy().m06();

        final var binder = (LocalReturnBinder) parser.parse(captor.invocation().method()).returnBinder();
        final var expected = Mockito.mock(ProducerRecord.class);

        final var returned = binder.apply(null,
                new ProducerFnRecord(expected, new CompletableFuture<RecordMetadata>()));

        Assertions.assertEquals(expected, returned);
    }

}
