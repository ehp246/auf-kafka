package me.ehp246.test.embedded.producer.value.jsonview;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import me.ehp246.aufkafka.api.serializer.JacksonObjectOf;
import me.ehp246.aufkafka.api.serializer.json.FromJson;
import me.ehp246.test.embedded.producer.value.jsonview.TestCases.Payload.AccountRequest;
import me.ehp246.test.mock.EmbeddedKafkaConfig;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class, MsgListener.class })
@EmbeddedKafka(topics = { "embedded" }, partitions = 1)
@DirtiesContext
class ValueViewTest {
    @Autowired
    private TestCases.Case01 case01;

    @Autowired
    private MsgListener listener;

    @Autowired
    private FromJson fromJson;

    @BeforeEach
    void reset() {
        listener.reset();
    }

    @Test
    void view_01() {
        final var value = new AccountRequest(UUID.randomUUID().toString(),
                UUID.randomUUID().toString());

        this.case01.withoutPassword(value);

        final var received = this.fromJson.apply(listener.take().value(),
                new JacksonObjectOf<>(AccountRequest.class));

        Assertions.assertEquals(null, received.password());
    }

    @Test
    void view_02() throws InterruptedException, ExecutionException {
        final var request = new AccountRequest(UUID.randomUUID().toString(),
                UUID.randomUUID().toString());

        this.case01.withAll(request);

        final var json = listener.take().value();

        Assertions.assertEquals(true, json.contains(request.id()));
        Assertions.assertEquals(true, json.contains(request.password()));
    }
}
