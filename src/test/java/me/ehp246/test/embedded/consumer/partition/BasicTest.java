package me.ehp246.test.embedded.consumer.partition;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

import me.ehp246.test.mock.EmbeddedKafkaConfig;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class, }, webEnvironment = WebEnvironment.NONE)
@EmbeddedKafka(topics = { AppConfig.TOPIC })
class BasicTest {
    @Autowired
    private KafkaTemplate<String, String> template;
}
