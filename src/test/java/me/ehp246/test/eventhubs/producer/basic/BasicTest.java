package me.ehp246.test.eventhubs.producer.basic;

import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.Uuid;
import org.junit.jupiter.api.Assertions;
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
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("local")
@EnabledIfSystemProperty(named = "me.ehp246.test.eventhubs", matches = "enabled")
class BasicTest {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private Producer<String, String> producer;

    @Test
    void template_01() {
        kafkaTemplate.send("basic", "NewEvent", Uuid.randomUuid().toString());
    }

    @Test
    void producer_01() throws InterruptedException, ExecutionException {
        final var future = producer.send(new ProducerRecord<String, String>("basic", "NewEvent", Uuid.randomUuid().toString()));

        final var recordMetadata = future.get();

        Assertions.assertEquals(0, recordMetadata.partition());
    }
}
