package me.ehp246.test.embedded.consumer.key;

import java.util.UUID;

import org.apache.kafka.clients.producer.ProducerRecord;
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
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class, KeyAction.class })
@EmbeddedKafka(topics = { "embedded" }, partitions = 1)
@DirtiesContext
class KeyTest {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private KeyAction action;

    @Test
    void key_01() {
        final var expected = UUID.randomUUID().toString();

        kafkaTemplate.send(new ProducerRecord<String, String>("embedded", expected, null));

        Assertions.assertEquals(expected, action.take());
    }
}
