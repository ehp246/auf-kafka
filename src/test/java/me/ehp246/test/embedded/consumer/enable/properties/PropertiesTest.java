package me.ehp246.test.embedded.consumer.enable.properties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.kafka.test.context.EmbeddedKafka;

import me.ehp246.aufkafka.api.consumer.InboundEndpoint;
import me.ehp246.test.mock.EmbeddedKafkaConfig;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class }, properties = {
        "value.2=75f90fa4-da65-44de-bb59-d42a92448d7b" }, webEnvironment = WebEnvironment.NONE)
@EmbeddedKafka(topics = { AppConfig.TOPIC }, partitions = 1)
class PropertiesTest {
    @Autowired
    private ListableBeanFactory beanFactory;

    @Test
    void properties_01() {
        final var endpoint = beanFactory.getBeansOfType(InboundEndpoint.class).get("inboundEndpoint-0");

        Assertions.assertEquals(0, endpoint.consumerProperties().size());
    }

    @Test
    void properties_02() {
        final var custom = beanFactory.getBeansOfType(InboundEndpoint.class).get("inboundEndpoint-1")
                .consumerProperties();

        Assertions.assertEquals(2, custom.size());
        Assertions.assertEquals("value1", custom.get("custom1"));
        Assertions.assertEquals("75f90fa4-da65-44de-bb59-d42a92448d7b", custom.get("custom2"));
    }
}
