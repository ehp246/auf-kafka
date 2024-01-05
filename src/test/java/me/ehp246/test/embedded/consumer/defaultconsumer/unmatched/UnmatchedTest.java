package me.ehp246.test.embedded.consumer.defaultconsumer.unmatched;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
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
    private Unmatched unmatched;

    @Autowired
    private KafkaTemplate<String, String> template;

    @BeforeEach
    void reset() {
        unmatched.reset();
    }

    // @Test
    void unmatched_01() {
        this.template.send("embedded", null);

        Assertions.assertEquals(null, this.unmatched.take().key());
    }

    @Timeout(3)
    @Test
    void unmatched_02() {
        final var key = UUID.randomUUID().toString();
        this.template.send("embedded", key, null);

        Assertions.assertEquals(key, this.unmatched.take().key());
    }
}
