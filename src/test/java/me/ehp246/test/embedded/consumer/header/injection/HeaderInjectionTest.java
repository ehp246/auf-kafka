package me.ehp246.test.embedded.consumer.header.injection;

import java.util.List;
import java.util.UUID;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import me.ehp246.aufkafka.core.util.OneUtil;
import me.ehp246.test.mock.EmbeddedKafkaConfig;
import me.ehp246.test.mock.StringHeader;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class,
        HeaderInjectAction.class }, webEnvironment = WebEnvironment.NONE)
@EmbeddedKafka(topics = { "embedded" }, partitions = 1)
@DirtiesContext
class HeaderInjectionTest {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private HeaderInjectAction action;

    @Test
    @Timeout(1)
    void header_01() {
        kafkaTemplate.send(new ProducerRecord<String, String>("embedded", UUID.randomUUID().toString(), null));

        final var received = action.take();

        Assertions.assertEquals(0, received.headers().toArray().length);
        Assertions.assertEquals(0, received.headerList().size());
        Assertions.assertEquals(null, received.header1());
    }

    @Test
    @Timeout(1)
    void header_02() {
        final var header1Value = UUID.randomUUID().toString();
        kafkaTemplate.send(new ProducerRecord<String, String>("embedded", null, UUID.randomUUID().toString(), null,
                List.of(new StringHeader("Header1", header1Value))));

        final var received = action.take();

        Assertions.assertEquals(1, OneUtil.toList(received.headers()).size());
        Assertions.assertEquals(0, received.headerList().size());
        Assertions.assertEquals(header1Value, received.header1());
    }
}
