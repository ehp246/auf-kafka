package me.ehp246.test.embedded.producer.callback;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { CallbackKafkaConfig.class, AppConfig.class, MsgListener.class, Callmeback.class })
@EmbeddedKafka(topics = { AppConfig.TOPIC }, partitions = 1)
class CallbackTest {
    @Autowired
    private TestCases.Case01 case01;

    @Autowired
    private MsgListener listener;

    @Autowired
    private Callmeback callmeback;

    @BeforeEach
    void reset() {
        listener.reset();
    }

    @Test
    void callback_01() throws InterruptedException, ExecutionException {
        final var value = case01.newEvent(UUID.randomUUID().toString());

        final var received = listener.take();
        final var calledback = callmeback.take();

        Assertions.assertEquals(value.record().key(), received.key());
        Assertions.assertEquals(received.offset(), calledback.offset());
        Assertions.assertEquals(value.future().get(), calledback);
    }

}
