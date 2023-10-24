package me.ehp246.test.embedded.producer.basic;

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

import me.ehp246.test.mock.EmbeddedKafkaConfig;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class, MsgListener.class })
@EmbeddedKafka(topics = { "embedded" }, partitions = 1)
@DirtiesContext
class BasticTest {
    @Autowired
    private MsgListener listener;

    @Autowired
    private KafkaTemplate<String, String> template;

    @BeforeEach
    void reset() {
        listener.reset();
    }

    @Test
    void producer_01() throws InterruptedException, ExecutionException {
        final var value = UUID.randomUUID().toString();
        template.send("embedded", "NewEvent", value);

        final var received = listener.take();

        Assertions.assertEquals("NewEvent", received.key());
        Assertions.assertEquals(value, received.value());
    }
}
