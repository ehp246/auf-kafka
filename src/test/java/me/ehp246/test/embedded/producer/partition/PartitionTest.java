package me.ehp246.test.embedded.producer.partition;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

import me.ehp246.test.mock.EmbeddedKafkaConfig;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class, MsgListener.class })
@EmbeddedKafka(topics = { AppConfig.TOPIC }, partitions = 10)
class PartitionTest {
    @Autowired
    private TestCases.Case01 case01;

    @Autowired
    private TestCases.Case02 case02;

    @Autowired
    private MsgListener listener;

    @BeforeEach
    void reset() {
        this.listener.reset();
    }

    @Test
    void partition_01() {
        final var key = UUID.randomUUID().toString();
        final var expected = 2;

        this.case01.onParam(key, expected);
        final var inboundEvent = listener.get();

        Assertions.assertEquals(key, inboundEvent.key());
        Assertions.assertEquals(expected, inboundEvent.partition());
    }

    @Test
    void partition_02() {
        final var key = UUID.randomUUID().toString();

        final var send = this.case01.onMethod01(key);
        final var inboundEvent = listener.get();

        Assertions.assertEquals(null, send.partition());
        Assertions.assertEquals(key, inboundEvent.key());
    }

    @Test
    void partition_03() {
        final var key = UUID.randomUUID().toString();

        final var sent = this.case01.onMethod02(key);
        final var inboundEvent = listener.get();

        Assertions.assertEquals(3, sent.partition());
        Assertions.assertEquals(key, inboundEvent.key());
    }

    @Test
    void partition_04() {
        final var key = UUID.randomUUID().toString();

        this.case02.onType(key);
        final var inboundEvent = this.listener.get();

        Assertions.assertEquals(2, inboundEvent.consumerRecord().partition());
        Assertions.assertEquals(key, inboundEvent.key());
    }

    @Test
    void partition_05() {
        final var key = UUID.randomUUID().toString();

        this.case02.onParam(key, 0);
        final var inboundEvent = listener.get();

        Assertions.assertEquals(key, inboundEvent.key());
        Assertions.assertEquals(0, inboundEvent.consumerRecord().partition());
    }

    @Test
    void partition_06() {
        final var key = UUID.randomUUID().toString();

        final var sent = this.case02.onParam(key, null);
        final var inboundEvent = listener.get();

        Assertions.assertEquals(null, sent.partition(), "should follow the specified");
        Assertions.assertEquals(key, inboundEvent.key());
    }

    @Test
    void partition_07() {
        final var key = UUID.randomUUID().toString();

        final var sent = this.case02.onMethod01(key);
        final var inboundEvent = listener.get();

        Assertions.assertEquals(null, sent.partition());
        Assertions.assertEquals(key, inboundEvent.key());
    }

    @Test
    void partition_08() {
        final var sent = this.case02.onMethod02(UUID.randomUUID().toString());
        final var inboundEvent = this.listener.get();

        Assertions.assertEquals(sent.key(), inboundEvent.key());

        Assertions.assertEquals(6, sent.partition());
        Assertions.assertEquals(6, inboundEvent.partition());
    }

    @Test
    void partition_09() {
        final var key = UUID.randomUUID().toString();

        final var sent = this.case02.onParam02(key, 1);
        final var inboundEvent = listener.get();

        Assertions.assertEquals(1, sent.partition());
        Assertions.assertEquals(key, inboundEvent.key());
    }

    @Test
    void partition_10() {
        final var key = UUID.randomUUID().toString();

        final var sent = this.case02.onParam02(key, null);
        final var inboundEvent = listener.get();

        Assertions.assertEquals(null, sent.partition());
        Assertions.assertEquals(key, inboundEvent.key());
    }
}
