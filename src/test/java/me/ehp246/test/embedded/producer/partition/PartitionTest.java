package me.ehp246.test.embedded.producer.partition;

import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import me.ehp246.test.mock.EmbeddedKafkaConfig;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class, MsgListener.class })
@EmbeddedKafka(topics = { "embedded" }, partitions = 10)
@DirtiesContext
@Disabled
class PartitionTest {
    @Autowired
    private TestCases.Case01 case01;

    @Autowired
    private TestCases.Case02 case02;

    @Autowired
    private MsgListener listener;

    @BeforeEach
    void reset() {
        listener.reset();
    }

    @Test
    void producer_partition_01() throws InterruptedException, ExecutionException {
        this.case01.newEventWithPartition("7dd344b8-b48a-49f0-8e5f-8fc685cbc797");

        final var received = listener.take();

        Assertions.assertEquals(3, received.partition(), "should not change");
    }

    @Test
    void producer_partition_02() throws InterruptedException, ExecutionException {
        this.case01.newEventWithPartition(Integer.valueOf(1234));

        final var received = listener.take();

        Assertions.assertEquals(2, received.partition(), "should not change");
    }

    @Test
    void producer_partition_direct_01() throws InterruptedException, ExecutionException {
        this.case02.newEventWithDirectPartition(9);

        final var received = listener.take();

        Assertions.assertEquals(9, received.partition(), "should use it");
    }

    @Test
    void producer_partition_direct_02() throws InterruptedException, ExecutionException {
        this.case02.newEventWithDirectPartition(Integer.valueOf(7));

        final var received = listener.take();

        Assertions.assertEquals(7, received.partition(), "should use it");
    }

    @Test
    void producer_partition_direct_03() throws InterruptedException, ExecutionException {
        Assertions.assertThrows(RuntimeException.class, () -> this.case02.newEvent(new TestCases.Event(null)));
    }
}
