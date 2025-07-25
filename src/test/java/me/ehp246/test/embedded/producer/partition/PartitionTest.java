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
        final var expected = 2;

        this.case01.onParam(expected);

        Assertions.assertEquals(expected, listener.get().partition());
    }

    @Test
    void partition_02() {
        Assertions.assertEquals(null, this.case01.onMethod01().partition());
    }

    @Test
    void partition_03() {
        Assertions.assertEquals(6, this.case01.onMethod02().partition());
    }

    @Test
    void partition_04() {
        this.case02.onType();

        Assertions.assertEquals(2, this.listener.get().consumerRecord().partition());
    }

    @Test
    void partition_05() {
        this.case02.onParam(0);

        Assertions.assertEquals(0, this.listener.get().consumerRecord().partition());
    }

    @Test
    void partition_06() {
        Assertions.assertEquals(null, this.case02.onParam(null).partition(), "should follow the specified");
    }

    @Test
    void partition_07() {
        Assertions.assertEquals(null, this.case02.onMethod01().partition());
    }

    @Test
    void partition_08() {
        final var sent = this.case02.onMethod02(UUID.randomUUID().toString());
        final var inboundEvent = this.listener.get();

        Assertions.assertEquals(6, sent.partition());

        Assertions.assertEquals(sent.key(), inboundEvent.key());
        Assertions.assertEquals(6, inboundEvent.partition());
    }

    @Test
    void partition_09() {
        Assertions.assertEquals(1, this.case02.onParam02(1).partition());
        Assertions.assertEquals(null, this.case02.onParam02(null).partition());
    }
}
