package me.ehp246.test.embedded.consumer.basic;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import me.ehp246.test.mock.EmbeddedKafkaConfig;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class, ConsumerExecutor.class })
@EmbeddedKafka(topics = { "embedded" })
@DirtiesContext
class BasicTest {
    @Autowired
    private KafkaTemplate<String, String> template;

    @Autowired
    private ConsumerExecutor executor;

    @Test
    void basic_01() throws InterruptedException, ExecutionException {
        final var v1 = UUID.randomUUID().toString();

        template.send("embedded", v1, null).get();

        executor.startPolling();

        final var polled1 = executor.waitAndTake();

        Assertions.assertEquals(1, polled1.count());
    }

}
