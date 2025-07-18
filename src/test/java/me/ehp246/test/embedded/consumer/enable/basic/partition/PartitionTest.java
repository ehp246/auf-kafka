package me.ehp246.test.embedded.consumer.enable.basic.partition;

import java.time.Duration;
import java.util.UUID;

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
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class, WildcardAction.class,
        MsgListener.class }, properties = { "p.3=3" }, webEnvironment = WebEnvironment.NONE)
@EmbeddedKafka(topics = { AppConfig.TOPIC }, partitions = 5)
class PartitionTest {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private WildcardAction action;

    @Autowired
    private MsgListener listener;

    @BeforeEach
    void reset() {
        this.action.reset();
        this.listener.reset();
    }

    @Test
    void partition_01() throws InterruptedException {
        final var key = UUID.randomUUID().toString();

        kafkaTemplate.send(AppConfig.TOPIC, 4, key, null);

        final var event = listener.take();

        Assertions.assertEquals(key, event.key());
        Thread.sleep(Duration.ofSeconds(1));
        Assertions.assertEquals(true, !this.action.future().isDone(), "should not have received anything.");
    }
}
