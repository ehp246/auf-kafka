package me.ehp246.test.embedded.consumer.header.event;

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

import me.ehp246.aufkafka.api.AufKafkaConstant;
import me.ehp246.test.mock.EmbeddedKafkaConfig;
import me.ehp246.test.mock.StringHeader;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class,
        EventAction.class }, webEnvironment = WebEnvironment.NONE)
@EmbeddedKafka(topics = { "42de72b9-c551-4d38-b56e-9ce0ea77e7a2", "efec8bfb-77d8-4091-b2a7-fc9e050030b4",
        "c67e2456-8427-439a-af2b-ba19eb2b7945" })
@DirtiesContext
class EventHeaderTest {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private EventAction action;

    @Test
    @Timeout(2)
    void topic1_header_01() {
        kafkaTemplate.send(new ProducerRecord<String, String>("42de72b9-c551-4d38-b56e-9ce0ea77e7a2", null,
                UUID.randomUUID().toString(), null, StringHeader.headers(AufKafkaConstant.EVENT_HEADER, "")));

        final var received = action.take();

        Assertions.assertEquals("42de72b9-c551-4d38-b56e-9ce0ea77e7a2", received.topic());
        Assertions.assertEquals(1, received.headerMap().size());
        Assertions.assertEquals(1, received.headerMap().get(AufKafkaConstant.EVENT_HEADER).size());
        Assertions.assertEquals("", received.headerMap().get(AufKafkaConstant.EVENT_HEADER).get(0));
    }

    @Test
    @Timeout(2)
    void topic1_header_02() {
        final var header1Value = UUID.randomUUID().toString();
        kafkaTemplate.send(new ProducerRecord<String, String>("42de72b9-c551-4d38-b56e-9ce0ea77e7a2", null,
                UUID.randomUUID().toString(), null,
                StringHeader.headers("Header1", header1Value, AufKafkaConstant.EVENT_HEADER, header1Value)));

        final var received = action.take();

        Assertions.assertEquals("42de72b9-c551-4d38-b56e-9ce0ea77e7a2", received.topic());
        Assertions.assertEquals(2, received.headerMap().size());
        Assertions.assertEquals(header1Value, received.headerMap().get(AufKafkaConstant.EVENT_HEADER).get(0));
        Assertions.assertEquals(header1Value, received.headerMap().get("Header1").get(0));
    }

    @Test
    @Timeout(2)
    void topic2_header_01() {
        kafkaTemplate.send(new ProducerRecord<String, String>("efec8bfb-77d8-4091-b2a7-fc9e050030b4", null,
                UUID.randomUUID().toString(), null, StringHeader.headers("my.own.event.header", "")));

        final var received = action.take();

        Assertions.assertEquals("efec8bfb-77d8-4091-b2a7-fc9e050030b4", received.topic());
        Assertions.assertEquals(1, received.headerMap().size());
        Assertions.assertEquals(1, received.headerMap().get("my.own.event.header").size());
        Assertions.assertEquals("", received.headerMap().get("my.own.event.header").get(0));
    }

    @Test
    @Timeout(2)
    void topic2_header_02() {
        final var header1Value = UUID.randomUUID().toString();
        kafkaTemplate.send(new ProducerRecord<String, String>("efec8bfb-77d8-4091-b2a7-fc9e050030b4", null,
                UUID.randomUUID().toString(), null,
                StringHeader.headers("Header1", header1Value, "my.own.event.header", header1Value)));

        final var received = action.take();

        Assertions.assertEquals("efec8bfb-77d8-4091-b2a7-fc9e050030b4", received.topic());
        Assertions.assertEquals(2, received.headerMap().size());
        Assertions.assertEquals(header1Value, received.headerMap().get("my.own.event.header").get(0));
        Assertions.assertEquals(header1Value, received.headerMap().get("Header1").get(0));
    }

    @Test
    @Timeout(2)
    void topic3_header_01() {
        kafkaTemplate.send(new ProducerRecord<String, String>("c67e2456-8427-439a-af2b-ba19eb2b7945", null,
                UUID.randomUUID().toString(), null, StringHeader.headers("", "")));

        final var received = action.take();

        Assertions.assertEquals("c67e2456-8427-439a-af2b-ba19eb2b7945", received.topic());
        Assertions.assertEquals(1, received.headerMap().size());
        Assertions.assertEquals(1, received.headerMap().get("").size());
        Assertions.assertEquals("", received.headerMap().get("").get(0));
    }
}
