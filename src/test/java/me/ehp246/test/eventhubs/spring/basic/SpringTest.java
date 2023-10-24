package me.ehp246.test.eventhubs.spring.basic;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class, MsgListener.class }, webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("local")
@EnabledIfSystemProperty(named = "me.ehp246.test.eventhubs", matches = "enabled")
class SpringTest {
    @Autowired
    private MsgListener listener;

    @Autowired
    private KafkaTemplate<String, String> template;

    @BeforeEach
    void reset() {
        listener.reset();
    }

    @Test
    void template_01() throws InterruptedException, ExecutionException {
        final var key = UUID.randomUUID().toString();
        final var value = UUID.randomUUID().toString();
        template.send("basic", key, value).get();

        final var received = listener.take();

        Assertions.assertEquals(key, received.key());
        Assertions.assertEquals(value, received.value());
    }
}
