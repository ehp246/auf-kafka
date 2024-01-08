package me.ehp246.test.embedded.consumer.defaultconsumer.unmatched;

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
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class },
        properties = "default.consumer.name=unmatched")
@EmbeddedKafka(topics = "embedded")
class UnmatchedTest {
    @Autowired
    private Send send;

    @Autowired
    private Unmatched unmatched;

    @BeforeEach
    void reset() {
        unmatched.reset();
    }

    @Test
    void unmatched_01() {
        this.send.send(null);

        Assertions.assertEquals(null, this.unmatched.take().key());
    }

    @Test
    void unmatched_02() {
        final var key = UUID.randomUUID().toString();

        this.send.send(key);

        Assertions.assertEquals(key, this.unmatched.take().key());
    }
}
