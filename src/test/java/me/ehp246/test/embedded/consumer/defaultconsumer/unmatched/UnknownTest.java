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
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class }, properties = {
        "default.consumer.name=unknown", "me.ehp246.aufkafka.consumer.messagelogging.enabled=true" })
@EmbeddedKafka(topics = "embedded")
class UnknownTest {
    @Autowired
    private Send send;

    @Autowired
    private Unknown unknown;

    @BeforeEach
    void reset() {
        unknown.reset();
    }

    @Test
    void unmatched_01() {
        this.send.send(null);

        Assertions.assertEquals(null, this.unknown.take().key());
    }

    @Test
    void unmatched_02() {
        final var key = UUID.randomUUID().toString();

        this.send.send(key);

        Assertions.assertEquals(key, this.unknown.take().key());
    }
}
