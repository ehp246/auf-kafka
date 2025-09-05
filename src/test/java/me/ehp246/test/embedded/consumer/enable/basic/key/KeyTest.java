package me.ehp246.test.embedded.consumer.enable.basic.key;

import java.util.UUID;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

import me.ehp246.test.mock.EmbeddedKafkaConfig;
import me.ehp246.test.mock.WildcardAction;

/**
 * @author Lei Yang
 *
 */
@Disabled("Because of GitHub Action")
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class, WildcardAction.class })
@EmbeddedKafka(topics = { AppConfig.TOPIC }, partitions = 1)
class KeyTest {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private WildcardAction action;

    @Test
    void key_01() {
        final var expected = UUID.randomUUID().toString();

        kafkaTemplate.send(new ProducerRecord<String, String>(AppConfig.TOPIC, expected, null));

        Assertions.assertEquals(expected, action.take().key());
    }
}
