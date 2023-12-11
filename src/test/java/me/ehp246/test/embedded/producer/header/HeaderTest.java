package me.ehp246.test.embedded.producer.header;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import me.ehp246.aufkafka.core.util.OneUtil;
import me.ehp246.test.mock.EmbeddedKafkaConfig;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class, MsgListener.class })
@EmbeddedKafka(topics = { "embedded" }, partitions = 10)
@DirtiesContext
class HeaderTest {
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
    void header_01() throws InterruptedException, ExecutionException {
        this.case01.header(null, null, null);

        final var headers = OneUtil.toList(listener.take().headers());

        Assertions.assertEquals(3, headers.size());

        Assertions.assertEquals("Header", headers.get(0).key());
        Assertions.assertEquals(null, headers.get(0).value());

        Assertions.assertEquals("header02", headers.get(1).key());
        Assertions.assertEquals(null, headers.get(1).value());

        Assertions.assertEquals("header02", headers.get(2).key());
        Assertions.assertEquals(null, headers.get(2).value());
    }

    @Test
    void header_02() throws InterruptedException, ExecutionException {
        final var header1 = UUID.randomUUID();
        final var header2 = UUID.randomUUID();

        this.case01.header(header1, header2, null);

        final var headers = OneUtil.toList(listener.take().headers());

        Assertions.assertEquals(3, headers.size());

        Assertions.assertEquals("Header", headers.get(0).key());
        Assertions.assertEquals(true, new String(headers.get(0).value(), StandardCharsets.UTF_8)
                .equals(header1.toString()));

        Assertions.assertEquals("header02", headers.get(1).key());
        Assertions.assertEquals(true, new String(headers.get(1).value(), StandardCharsets.UTF_8)
                .equals(header2.toString()));

        Assertions.assertEquals("header02", headers.get(2).key());
        Assertions.assertEquals(null, headers.get(2).value());
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
        Assertions.assertThrows(RuntimeException.class,
                () -> this.case02.newEvent(new TestCases.Event(null)));
    }
}
