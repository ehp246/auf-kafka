package me.ehp246.test.embedded.consumer.listener.failed;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.kafka.test.context.EmbeddedKafka;

import me.ehp246.aufkafka.api.serializer.json.ToJson;
import me.ehp246.test.embedded.consumer.listener.failed.invocation.FailMsg;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, properties = {},
        webEnvironment = WebEnvironment.NONE)
@EmbeddedKafka(topics = "embedded")
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

        final var failed = appConfig.consumer1Ref.get();

        Assertions.assertEquals(onMsg.ex, failed.thrown());
        Assertions.assertEquals(toJson.apply(id), failed.bound().received().value());
    }
}
