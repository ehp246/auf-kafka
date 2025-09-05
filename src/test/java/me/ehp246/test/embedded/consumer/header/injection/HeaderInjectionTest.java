package me.ehp246.test.embedded.consumer.header.injection;

import java.util.List;
import java.util.UUID;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

import me.ehp246.test.TestUtil;
import me.ehp246.test.mock.EmbeddedKafkaConfig;
import me.ehp246.test.mock.StringHeader;

/**
 * @author Lei Yang
 *
 */
@Disabled
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class,
        HeaderInjectAction.class }, webEnvironment = WebEnvironment.NONE)
@EmbeddedKafka(topics = { AppConfig.TOPIC }, partitions = 1)
class HeaderInjectionTest {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private HeaderInjectAction action;

    @Test
    void header_01() {
        kafkaTemplate.send(new ProducerRecord<String, String>(AppConfig.TOPIC, UUID.randomUUID().toString(), null));

        final var received = action.take();

        Assertions.assertEquals(0, received.headers().toArray().length);
        Assertions.assertEquals(null, received.headerList());
        Assertions.assertEquals(null, received.header1());
    }

    @Test
    void header_02() {
        final var header1Value = UUID.randomUUID().toString();
        kafkaTemplate.send(new ProducerRecord<String, String>(AppConfig.TOPIC, null, UUID.randomUUID().toString(), null,
                List.of(new StringHeader("Header1", header1Value))));

        final var received = action.take();

        Assertions.assertEquals(1, TestUtil.toList(received.headers()).size());
        Assertions.assertEquals(null, received.headerList());
        Assertions.assertEquals(header1Value, received.header1());
    }

    @Test
    void headerList_01() {
        final var header1Value = UUID.randomUUID().toString();
        kafkaTemplate.send(new ProducerRecord<String, String>(AppConfig.TOPIC, null, UUID.randomUUID().toString(), null,
                List.of(new StringHeader("Header1", header1Value), new StringHeader("HeaderList", header1Value),
                        new StringHeader("HeaderList", header1Value))));

        final var received = action.take();

        Assertions.assertEquals(2, received.headerList().size());
        Assertions.assertEquals(header1Value, received.headerList().get(0));
        Assertions.assertEquals(header1Value, received.headerList().get(1));
    }
}
