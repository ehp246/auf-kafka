package me.ehp246.aufkafka.core.producer;

import java.time.Instant;
import java.util.UUID;

import org.apache.kafka.common.Uuid;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufkafka.api.producer.ProxyMethodParser;
import me.ehp246.aufkafka.core.util.OneUtil;
import me.ehp246.test.TestUtil;

/**
 * @author Lei Yang
 *
 */
class DefaultProxyMethodParserTest {
    private final ProxyMethodParser parser = new DefaultProxyMethodParser(
            new MockEnvironment().withProperty("topic.name",
                    "bc5beb1b-569c-4055-bedf-3b06f9af2e5d")::resolvePlaceholders);

    @Test
    void topic_01() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.TopicCase01.class);

        captor.proxy().m01();

        final var message = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args()).message();

        Assertions.assertEquals("c26d1201-a956-4a45-a049-bc7fece18fff", message.topic());
    }

    @Test
    void topic_02() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.TopicCase01.class);

        captor.proxy().m02(null);

        final var message = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args()).message();

        Assertions.assertEquals(null, message.topic());
    }

    @Test
    void topic_03() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.TopicCase01.class);
        final var expected = Uuid.randomUuid().toString();

        captor.proxy().m02(expected);

        final var message = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args()).message();

        Assertions.assertEquals(expected, message.topic());
    }

    @Test
    void topic_04() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.TopicCase02.class);

        captor.proxy().m01();

        final var message = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args()).message();

        Assertions.assertEquals("bc5beb1b-569c-4055-bedf-3b06f9af2e5d", message.topic());
    }

    @Test
    void key_01() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.KeyCase01.class);

        captor.proxy().m01();

        final var message = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args()).message();

        Assertions.assertEquals("M01", message.key());
    }

    @Test
    void key_02() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.KeyCase01.class);
        final var expected = UUID.randomUUID();

        captor.proxy().m02(expected);

        final var message = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args()).message();

        Assertions.assertEquals(expected.toString(), message.key());
    }

    @Test
    void key_03() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.KeyCase01.class);

        captor.proxy().m02(null);

        final var message = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args()).message();

        Assertions.assertEquals(null, message.key());
    }

    @Test
    void key_04() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.KeyCase01.class);

        captor.proxy().m03();

        final var message = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args()).message();

        Assertions.assertEquals(null, message.key(), "should supress");
    }

    @Test
    void key_05() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.KeyCase01.class);

        captor.proxy().m04();

        final var message = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args()).message();

        Assertions.assertEquals("887114e5-5770-4f7f-b0c6-e0803753eb58", message.key(),
                "should follow annotation");
    }

    @Test
    void partition_01() throws Throwable {
        final var captor = TestUtil
                .newCaptor(DefaultProxyMethodParserTestCases.PartitionCase01.class);

        captor.proxy().m01();

        final var message = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args()).message();

        Assertions.assertEquals(null, message.partitionKey());
    }

    @Test
    void partition_02() throws Throwable {
        final var captor = TestUtil
                .newCaptor(DefaultProxyMethodParserTestCases.PartitionCase01.class);
        final var expected = Integer.valueOf((int) (Math.random() * 100));

        captor.proxy().m02(expected);

        final var message = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args()).message();

        Assertions.assertEquals(true, message.partitionKey() == expected);
    }

    @Test
    void partition_03() throws Throwable {
        final var captor = TestUtil
                .newCaptor(DefaultProxyMethodParserTestCases.PartitionCase01.class);

        captor.proxy().m02(null);

        final var message = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args()).message();

        Assertions.assertEquals(null, message.partitionKey());
    }

    @Test
    void timestamp_01() throws Throwable {
        final var captor = TestUtil
                .newCaptor(DefaultProxyMethodParserTestCases.TimestampCase01.class);

        captor.proxy().m01();

        final var message = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args()).message();

        Assertions.assertEquals(null, message.timestamp());
    }

    @Test
    void timestamp_02() throws Throwable {
        final var captor = TestUtil
                .newCaptor(DefaultProxyMethodParserTestCases.TimestampCase01.class);
        final var expected = Instant.now();

        captor.proxy().m02(expected);

        final var message = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args()).message();

        Assertions.assertEquals(expected, message.timestamp());
    }

    @Test
    void timestamp_03() throws Throwable {
        final var captor = TestUtil
                .newCaptor(DefaultProxyMethodParserTestCases.TimestampCase01.class);

        captor.proxy().m02(null);

        final var message = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args()).message();

        Assertions.assertEquals(null, message.timestamp());
    }

    @Test
    void timestamp_04() throws Throwable {
        final var captor = TestUtil
                .newCaptor(DefaultProxyMethodParserTestCases.TimestampCase01.class);
        final var expected = Instant.now().toEpochMilli();

        captor.proxy().m03(expected);

        final var message = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args()).message();

        Assertions.assertEquals(expected, message.timestamp().toEpochMilli());
    }

    @Test
    void timestamp_05() throws Throwable {
        final var captor = TestUtil
                .newCaptor(DefaultProxyMethodParserTestCases.TimestampCase01.class);

        captor.proxy().m03(null);

        final var message = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args()).message();

        Assertions.assertEquals(null, message.timestamp());
    }

    @Test
    void timestamp_06() throws Throwable {
        final var captor = TestUtil
                .newCaptor(DefaultProxyMethodParserTestCases.TimestampCase01.class);
        final var expected = Instant.now();
        captor.proxy().m04(expected.toEpochMilli());

        final var message = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args()).message();

        Assertions.assertEquals(expected.toEpochMilli(), message.timestamp().toEpochMilli());
    }

    @Test
    void value_01() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.ValueCase01.class);

        captor.proxy().m01();

        final var message = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args()).message();

        Assertions.assertEquals(null, message.value());
    }

    @Test
    void value_02() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.ValueCase01.class);
        final var expected = Instant.now();

        captor.proxy().m02(expected);

        final var message = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args()).message();

        Assertions.assertEquals(expected, message.value());
    }

    @Test
    void value_03() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.ValueCase01.class);

        captor.proxy().m02(null);

        final var message = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args()).message();

        Assertions.assertEquals(null, message.value());
    }

    @Test
    void value_04() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.ValueCase01.class);

        captor.proxy().m03(UUID.randomUUID());

        final var message = parser.parse(captor.invocation().method()).invocationBinder()
                .apply(captor.invocation().target(), captor.invocation().args()).message();

        Assertions.assertEquals(null, message.value());
    }

    @Test
    void header_01() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.HeaderCase01.class);

        captor.proxy().m01();

        final var headers = OneUtil.toList(parser.parse(captor.invocation().method())
                .invocationBinder().apply(captor.invocation().target(), captor.invocation().args())
                .message().headers());

        Assertions.assertEquals(3, headers.size());
        Assertions.assertEquals("header1", headers.get(0).name());
        Assertions.assertEquals("value1", headers.get(0).value());

        Assertions.assertEquals("header2", headers.get(1).name());
        Assertions.assertEquals("value2", headers.get(1).value());

        Assertions.assertEquals("header1", headers.get(2).name());
        Assertions.assertEquals("value2", headers.get(2).value());
    }

    @Test
    void header_02() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.HeaderCase01.class);

        captor.proxy().m02(UUID.randomUUID());

        final var headers = OneUtil.toList(parser.parse(captor.invocation().method())
                .invocationBinder().apply(captor.invocation().target(), captor.invocation().args())
                .message().headers());

        Assertions.assertEquals(4, headers.size());

        Assertions.assertEquals("Header", headers.get(0).name());
        Assertions.assertEquals(captor.invocation().args()[0], headers.get(0).value());

        Assertions.assertEquals("header1", headers.get(1).name());
        Assertions.assertEquals("value1", headers.get(1).value());

        Assertions.assertEquals("header2", headers.get(2).name());
        Assertions.assertEquals("value2", headers.get(2).value());

        Assertions.assertEquals("header1", headers.get(3).name());
        Assertions.assertEquals("value2", headers.get(3).value());
    }

    @Test
    void header_03() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.HeaderCase01.class);

        captor.proxy().m02(null);

        final var headers = OneUtil.toList(parser.parse(captor.invocation().method())
                .invocationBinder().apply(captor.invocation().target(), captor.invocation().args())
                .message().headers());

        Assertions.assertEquals(4, headers.size());

        Assertions.assertEquals("Header", headers.get(0).name());
        Assertions.assertEquals(captor.invocation().args()[0], headers.get(0).value());

        Assertions.assertEquals("header1", headers.get(1).name());
        Assertions.assertEquals("value1", headers.get(1).value());

        Assertions.assertEquals("header2", headers.get(2).name());
        Assertions.assertEquals("value2", headers.get(2).value());

        Assertions.assertEquals("header1", headers.get(3).name());
        Assertions.assertEquals("value2", headers.get(3).value());
    }

    @Test
    void header_05() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.HeaderCase02.class);
        captor.proxy().m01();

        final var headers = OneUtil.toList(parser.parse(captor.invocation().method())
                .invocationBinder().apply(captor.invocation().target(), captor.invocation().args())
                .message().headers());

        Assertions.assertEquals(0, headers.size());
    }

    @Test
    void header_06() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.HeaderCase02.class);
        captor.proxy().m03(UUID.randomUUID(), UUID.randomUUID());

        final var headers = OneUtil.toList(parser.parse(captor.invocation().method())
                .invocationBinder().apply(captor.invocation().target(), captor.invocation().args())
                .message().headers());

        Assertions.assertEquals(2, headers.size());

        Assertions.assertEquals("header1", headers.get(0).name());
        Assertions.assertEquals(captor.invocation().args()[0], headers.get(0).value());

        Assertions.assertEquals("header1", headers.get(1).name());
        Assertions.assertEquals(captor.invocation().args()[1], headers.get(1).value());
    }

    @Test
    void header_07() throws Throwable {
        final var captor = TestUtil.newCaptor(DefaultProxyMethodParserTestCases.HeaderCase02.class);
        captor.proxy().m03(null, UUID.randomUUID());

        final var headers = OneUtil.toList(parser.parse(captor.invocation().method())
                .invocationBinder().apply(captor.invocation().target(), captor.invocation().args())
                .message().headers());

        Assertions.assertEquals(2, headers.size());

        Assertions.assertEquals("header1", headers.get(0).name());
        Assertions.assertEquals(captor.invocation().args()[0], headers.get(0).value());

        Assertions.assertEquals("header1", headers.get(1).name());
        Assertions.assertEquals(captor.invocation().args()[1], headers.get(1).value());
    }
}
