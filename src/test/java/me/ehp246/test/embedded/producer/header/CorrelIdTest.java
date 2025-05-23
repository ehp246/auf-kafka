package me.ehp246.test.embedded.producer.header;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.kafka.test.context.EmbeddedKafka;

import me.ehp246.aufkafka.api.common.AufKafkaConstant;
import me.ehp246.test.mock.EmbeddedKafkaConfig;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class, MsgListener.class }, properties = {
        "me.ehp246.aufkafka.header.correlationId=trace.id" }, webEnvironment = WebEnvironment.NONE)
@EmbeddedKafka(topics = { "embedded" }, partitions = 10)
class CorrelIdTest {
    @Autowired
    private TestCases.CorrelIdCase01 correlIdCase01;

    @Autowired
    private MsgListener listener;

    @BeforeEach
    void reset() {
        listener.reset();
    }

    @Test
    void correlId_01() {
        this.correlIdCase01.ping();

        final var headers = listener.takeInboud().headerMap();

        Assertions.assertEquals(1, headers.get("trace.id").size());
        Assertions.assertEquals(true, !headers.get("trace.id").get(0).isBlank());
    }

    @Test
    void correlId_02() {
        final var expected = UUID.randomUUID();
        this.correlIdCase01.ping(expected);

        final var headers = listener.takeInboud().headerMap();

        Assertions.assertEquals(null, headers.get(AufKafkaConstant.CORRELATIONID_HEADER));
        Assertions.assertEquals(1, headers.get("trace.id").size());
        Assertions.assertEquals(true, headers.get("trace.id").get(0).equals(expected.toString()));
    }

    @Test
    void correlId_03() {
        final var expected = UUID.randomUUID();

        this.correlIdCase01.ping(expected.toString());

        final var headers = listener.takeInboud().headerMap();

        Assertions.assertEquals(1, headers.get(AufKafkaConstant.CORRELATIONID_HEADER).size());
        Assertions.assertEquals(true,
                headers.get(AufKafkaConstant.CORRELATIONID_HEADER).get(0).equals(expected.toString()));

        Assertions.assertEquals(1, headers.get("trace.id").size());
        Assertions.assertEquals(true, !headers.get("trace.id").get(0).equals(expected.toString()),
                "should has a generated id");
    }
}
