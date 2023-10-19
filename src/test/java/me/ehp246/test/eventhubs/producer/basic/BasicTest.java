package me.ehp246.test.eventhubs.producer.basic;

import org.apache.kafka.common.Uuid;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.NONE)
@EnabledIfSystemProperty(named = "me.ehp246.test.eventhubs", matches = "enabled")
class BasicTest {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void basic_01() {
        kafkaTemplate.send("basic", "NewEvent", Uuid.randomUuid().toString());
    }
}
