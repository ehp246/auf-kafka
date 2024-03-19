package me.ehp246.test.eventhubs.producer.basic;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.Uuid;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
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
	private AppConfig appConfig;
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	@Autowired
	private Producer<String, String> producer;

	@Test
	void template_01() throws InterruptedException, ExecutionException {
		kafkaTemplate.send("test", "NewEvent", Uuid.randomUuid().toString()).get();
	}

	@RepeatedTest(4)
	void producer_01() throws InterruptedException, ExecutionException {
		final var future = producer
				.send(new ProducerRecord<String, String>("basic", "NewEvent", UUID.randomUUID().toString()));

		final var recordMetadata = future.get();

		Assertions.assertEquals(0, recordMetadata.partition());
	}

	@Test
	void consumer_01() throws InterruptedException, ExecutionException {
		final var consumer = appConfig.newConsumer();

		consumer.subscribe(Set.of("basic"));

		final var polled = consumer.poll(Duration.ofSeconds(30));

		consumer.position(new TopicPartition("basic", 0));

		consumer.commitSync();

		Assertions.assertTrue(polled.count() >= 1, "should have the sent");

		for (final var rec : polled) {
			Assertions.assertEquals(null, rec.key());
			Assertions.assertEquals(null, rec.value());
		}

	}
}
