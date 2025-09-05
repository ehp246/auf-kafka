package me.ehp246.test.embedded.consumer.exception;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

import me.ehp246.test.mock.EmbeddedKafkaConfig;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class, EmbeddedKafkaConfig.class })
@EmbeddedKafka(topics = AppConfig.TOPIC)
@Disabled
class ExceptionTest {
    @Autowired
    private OnConsumerException onException;

    @Autowired
    private KafkaTemplate<String, String> template;

    @Test
    void test_01() {
        final var key = UUID.randomUUID().toString();

        template.send(AppConfig.TOPIC, key, null);

        final var context = onException.take();

        Assertions.assertEquals(key, context.event().key());
        Assertions.assertEquals(IllegalArgumentException.class, context.thrown().getClass(),
                "should be the exception from the action");
        Assertions.assertEquals(NullPointerException.class, context.thrown().getSuppressed()[0].getClass(),
                "should be the exception from the listener");
    }
}
