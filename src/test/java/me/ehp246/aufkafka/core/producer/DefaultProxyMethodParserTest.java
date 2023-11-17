package me.ehp246.aufkafka.core.producer;

import org.apache.kafka.common.Uuid;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufkafka.api.producer.ProxyMethodParser;
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
}
