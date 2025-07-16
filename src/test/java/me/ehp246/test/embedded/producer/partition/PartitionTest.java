package me.ehp246.test.embedded.producer.partition;

import org.junit.jupiter.api.Assertions;
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
    private MsgListener listener;

    @Test
    void partition_01() {
        final var expected = 2;

        this.case01.onParam(expected);

        Assertions.assertEquals(expected, listener.take().partition());
    }

    @Test
    void partition_02() {
        Assertions.assertEquals(null, this.case01.onMethod01().record().partition());
    }
}
