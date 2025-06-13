package me.ehp246.test.embedded.producer.value.basic;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import me.ehp246.aufkafka.api.serializer.json.ToJson;
import me.ehp246.test.embedded.producer.value.basic.TestCases.Event;
import me.ehp246.test.mock.EmbeddedKafkaConfig;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class, MsgListener.class })
@EmbeddedKafka(topics = { "embedded" }, partitions = 1)
@DirtiesContext
class ValueBasicTest {
    @Autowired
    private TestCases.Case01 case01;

    @Autowired
    private MsgListener listener;

    @Autowired
    private ToJson toJson;

    @BeforeEach
    void reset() {
        listener.reset();
    }

    @Test
    void basic_01() throws InterruptedException, ExecutionException {
        case01.newEvent(null);

        Assertions.assertEquals(null, listener.take().value());
    }

    @Test
    void basic_02() throws InterruptedException, ExecutionException {
        final var event = new TestCases.Event(UUID.randomUUID().toString());

        case01.newEvent(event);

        Assertions.assertEquals(true, listener.take().value().equals(toJson.toJson(event)));
    }

    @Test
    void basic_03() throws InterruptedException, ExecutionException {
        case01.withoutValue(new Event(UUID.randomUUID().toString()));

        Assertions.assertEquals(null, listener.take().value(),
                "should have no value without annotation");
    }
}
