package me.ehp246.test.embedded.consumer.enable.basic.topic;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

import me.ehp246.test.mock.EmbeddedKafkaConfig;
import me.ehp246.test.mock.WildcardAction;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class, WildcardAction.class }, properties = {
        "topic2=7d9052da-86e0-4851-aac9-9e59cce05f05.2",
        "kafka.Config.topic=7d9052da-86e0-4851-aac9-9e59cce05f05.3" }, webEnvironment = WebEnvironment.NONE)
@EmbeddedKafka(topics = { "7d9052da-86e0-4851-aac9-9e59cce05f05.1", "7d9052da-86e0-4851-aac9-9e59cce05f05.2",
        "7d9052da-86e0-4851-aac9-9e59cce05f05.3" }, partitions = 1)
class TopicTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private WildcardAction action;

    @BeforeEach
    void reset() {
        this.action.reset();
    }

    @Test
    void topic_01() throws InterruptedException, ExecutionException {
        final var key = UUID.randomUUID().toString();

        kafkaTemplate.send(new ProducerRecord<String, String>(AppConfig.TOPIC + ".1", key, null)).get();

        final var event = action.take();

        Assertions.assertEquals(AppConfig.TOPIC + ".1", event.topic());
    }

    @Test
    void topic_02() throws InterruptedException, ExecutionException {
        final var key = UUID.randomUUID().toString();

        kafkaTemplate.send(new ProducerRecord<String, String>(AppConfig.TOPIC + ".2", key, null)).get();

        final var event = action.take();

        Assertions.assertEquals(AppConfig.TOPIC + ".2", event.topic());
    }

    @Test
    void topic_03() throws InterruptedException, ExecutionException {
        final var key = UUID.randomUUID().toString();

        kafkaTemplate.send(new ProducerRecord<String, String>(AppConfig.TOPIC + ".3", key, null)).get();

        final var event = action.take();

        Assertions.assertEquals(AppConfig.TOPIC + ".3", event.topic());
    }
}
