package me.ehp246.test.embedded.consumer.topic;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import me.ehp246.test.mock.EmbeddedKafkaConfig;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class },
        properties = { "topic2=embedded.2" })
@EmbeddedKafka(topics = { "embedded.1", "embedded.2" }, partitions = 1)
@DirtiesContext
class TopicTest {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    void topic_01() {
        kafkaTemplate.send(new ProducerRecord<String, String>("embedded.1", null));
    }
}
