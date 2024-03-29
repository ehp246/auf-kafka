package me.ehp246.test.embedded.consumer.bean;

import org.apache.kafka.clients.consumer.Consumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import me.ehp246.aufkafka.api.consumer.InboundConsumerRegistry;
import me.ehp246.aufkafka.api.consumer.InboundEndpoint;
import me.ehp246.test.mock.EmbeddedKafkaConfig;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class })
@EmbeddedKafka(topics = { "embedded" }, partitions = 1)
@DirtiesContext
class BeanTest {
	@Autowired
	private ListableBeanFactory beanFactory;

	@Test
	void inbound_01() {
		final var endpointMap = beanFactory.getBeansOfType(InboundEndpoint.class);
		final var registry = beanFactory.getBean(InboundConsumerRegistry.class);

		Assertions.assertEquals(1, endpointMap.size());
		Assertions.assertEquals(true, endpointMap.containsKey("inboundEndpoint-0"));
		Assertions.assertEquals(true, registry.get("inboundEndpoint-0").consumer() instanceof Consumer<String, String>);
	}
}
