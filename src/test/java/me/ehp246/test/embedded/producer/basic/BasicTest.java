package me.ehp246.test.embedded.producer.basic;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import me.ehp246.aufkafka.api.common.AufKafkaConstant;
import me.ehp246.aufkafka.api.serializer.jackson.ToJson;
import me.ehp246.aufkafka.api.serializer.jackson.TypeOfJson;
import me.ehp246.test.mock.EmbeddedKafkaConfig;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class, MsgListener.class })
@EmbeddedKafka(topics = { "embedded" }, partitions = 1)
@DirtiesContext
class BasicTest {
    @Autowired
    private TestCases.Case01 case01;

    @Autowired
    private MsgListener listener;

    @Autowired
    private KafkaTemplate<String, String> template;

    @Autowired
    private ToJson toJson;

    @BeforeEach
    void reset() {
        listener.reset();
    }

    @Test
    void template_01() {
        final var value = UUID.randomUUID().toString();
        template.send("embedded", "NewEvent", value);

        final var received = listener.take();

        Assertions.assertEquals("NewEvent", received.consumerRecord().key());
        Assertions.assertEquals(toJson.toJson(value, TypeOfJson.of(value.getClass())),
                received.consumerRecord().value());
    }

    @Test
    void event_01() {
        this.case01.newEvent();

        final var received = listener.take();

        Assertions.assertEquals(true, received.consumerRecord().topic().equals("embedded"));
        Assertions.assertEquals("NewEvent", received.lastHeader(AufKafkaConstant.EVENT_HEADER).orElse(null));
    }

    @Test
    void event_03() throws InterruptedException, ExecutionException {
        final var eventType = UUID.randomUUID().toString();
        this.case01.newEventType(eventType);

        final var received = listener.take();

        Assertions.assertEquals(true, received.consumerRecord().topic().equals("embedded"));
        Assertions.assertEquals(eventType, received.lastHeader(AufKafkaConstant.EVENT_HEADER).orElse(null));
    }

    @Test
    void producer_key_01() throws InterruptedException, ExecutionException {
        this.case01.newEvent();

        final var received = listener.take();

        Assertions.assertEquals(true, received.consumerRecord().topic().equals("embedded"));
        Assertions.assertEquals(null, received.consumerRecord().key());
    }

    @Test
    void producer_key_02() throws InterruptedException, ExecutionException {
        final var companyId = UUID.randomUUID().toString();
        this.case01.newEvent(companyId);

        final var received = listener.take();

        Assertions.assertEquals(true, received.topic().equals("embedded"));
        Assertions.assertEquals("NewEvent", received.lastHeader(AufKafkaConstant.EVENT_HEADER).orElse(null));
        Assertions.assertEquals(true, received.key().equals(companyId));
    }

    @Test
    void producer_timestamp_01() throws InterruptedException, ExecutionException {
        final var expected = Instant.now();

        this.case01.newEvent(expected);

        final var received = listener.take();

        Assertions.assertEquals(expected.toEpochMilli(), received.timestamp());
    }

    @Test
    void producer_timestamp_02() throws InterruptedException, ExecutionException {
        final var expected = (long) (Math.random() * 1000000);

        this.case01.newEvent(expected);

        final var received = listener.take();

        Assertions.assertEquals(expected, received.timestamp());
    }

    @Test
    void producerRecord_01() throws InterruptedException, ExecutionException {
        final var sent = this.case01.newSendEvent(UUID.randomUUID().toString());

        final var received = listener.take();

        Assertions.assertEquals(received.key(), sent.record().key());
        Assertions.assertEquals(received.offset(), sent.future().get().offset());
    }
}
