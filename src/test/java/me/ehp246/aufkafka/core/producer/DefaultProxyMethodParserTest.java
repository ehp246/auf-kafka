package me.ehp246.aufkafka.core.producer;

import java.util.UUID;

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

        Assertions.assertEquals("887114e5-5770-4f7f-b0c6-e0803753eb58", message.key(), "should follow annotation");
    }
}
