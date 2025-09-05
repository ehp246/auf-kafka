package me.ehp246.test.embedded.consumer.listener.failed;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.kafka.test.context.EmbeddedKafka;

import me.ehp246.aufkafka.api.serializer.jackson.ToJson;
import me.ehp246.aufkafka.api.serializer.jackson.TypeOfJson;
import me.ehp246.test.embedded.consumer.listener.failed.invocation.FailMsg;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, properties = {}, webEnvironment = WebEnvironment.NONE)
@EmbeddedKafka(topics = AppConfig.TOPIC)
@Disabled
class FailedInvocationTest {
    @Autowired
    private Send send;

    @Autowired
    private ToJson toJson;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private FailMsg onMsg;

    @Test
    void test_01() throws InterruptedException, ExecutionException {
        final var id = UUID.randomUUID().toString();

        send.failedMsg(id);

        final var failedBound = appConfig.consumer1Ref.get();

        Assertions.assertEquals(onMsg.ex, failedBound.right().thrown());
        Assertions.assertEquals(toJson.toJson(id, TypeOfJson.of(id.getClass())),
                failedBound.left().eventContext().event().value());
    }
}
