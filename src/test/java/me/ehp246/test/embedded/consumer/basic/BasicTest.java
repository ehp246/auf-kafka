package me.ehp246.test.embedded.consumer.basic;

import org.junit.jupiter.api.Assertions;
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
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class, ConsumerExecutor.class })
@EmbeddedKafka(topics = { "embedded" }, partitions = 1)
@DirtiesContext
class BasicTest {
    @Autowired
    private TestCases.Case01 case01;

    @Autowired
    private ConsumerExecutor executor;

    @Test
    void basic_01() {
        executor.poll();

        case01.newEvent();

        final var polled = executor.take();

        Assertions.assertEquals(1, polled.count());

        Assertions.assertEquals("NewEvent", OneUtil.toList(polled).getFirst().key());
    }
}
