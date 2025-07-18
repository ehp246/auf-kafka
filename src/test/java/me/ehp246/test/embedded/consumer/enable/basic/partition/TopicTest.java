package me.ehp246.test.embedded.consumer.enable.basic.partition;

import java.util.UUID;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import me.ehp246.test.mock.EmbeddedKafkaConfig;
import me.ehp246.test.mock.WildcardAction;

/**
 * @author Lei Yang
 *
 */
@Disabled
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class, WildcardAction.class }, properties = {
        "topic2=embedded.2", "kafka.Config.topic=embedded.3" }, webEnvironment = WebEnvironment.NONE)
@EmbeddedKafka(topics = { "embedded.1", "embedded.2", "embedded.3" }, partitions = 1)
@DirtiesContext
class TopicTest {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private WildcardAction action;

    @Test
    void topic_01() {
        final var key = UUID.randomUUID().toString();
        kafkaTemplate.send(new ProducerRecord<String, String>("embedded.1", key, null));

        final var msg = action.take();

        Assertions.assertEquals("embedded.1", msg.topic());
    }

    @Test
    void topic_02() {
        final var key = UUID.randomUUID().toString();
        kafkaTemplate.send(new ProducerRecord<String, String>("embedded.2", key, null));

        final var msg = action.take();

        Assertions.assertEquals("embedded.2", msg.topic());
    }

    @Test
    void topic_03() {
        final var key = UUID.randomUUID().toString();
        kafkaTemplate.send(new ProducerRecord<String, String>("embedded.3", key, null));

        final var msg = action.take();

        Assertions.assertEquals("embedded.3", msg.topic());
    }
}
