package me.ehp246.test.embedded.consumer.listener.completed;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import me.ehp246.test.mock.EmbeddedKafkaConfig;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class, CompletedListener.class, EmbeddedKafkaConfig.class },
        properties = { "comp1.name=completedListener" }, webEnvironment = WebEnvironment.NONE)
@EmbeddedKafka(topics = { "embedded" }, partitions = 1)
@DirtiesContext
class CompletedInvocationTest {
    @Autowired
    private Send send;
    @Autowired
    private CompletedListener listener;

    @BeforeEach
    void reset() {
        listener.reset();
    }

    @Test
    void completed_01() {
        final var id = UUID.randomUUID().toString();

        send.send(id);

        final var completed = listener.takeCompleted();

        Assertions.assertEquals(true, completed.returned() instanceof String);
        Assertions.assertEquals(id, completed.returned().toString());
    }

    @Test
    void bound_01() throws InterruptedException, ExecutionException {
        final var id = UUID.randomUUID().toString();
        send.send(id);

        final var bound = listener.takeBound();

        Assertions.assertEquals(true, bound.invocable().instance() instanceof OnMsg);
        Assertions.assertEquals(id, bound.arguments()[1]);
        Assertions.assertEquals("Send", bound.arguments()[0]);
    }
}
