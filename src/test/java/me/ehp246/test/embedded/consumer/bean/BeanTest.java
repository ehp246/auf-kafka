package me.ehp246.test.embedded.consumer.bean;

import org.apache.kafka.clients.consumer.Consumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.PlaceholderResolutionException;

import me.ehp246.aufkafka.api.consumer.InboundConsumerRegistry;
import me.ehp246.aufkafka.api.consumer.InboundEndpoint;
import me.ehp246.aufkafka.api.spi.PropertyResolver;
import me.ehp246.test.embedded.consumer.bean.AppConfig.KafkaConfig;
import me.ehp246.test.mock.EmbeddedKafkaConfig;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class }, properties = {
        "kafka.config.topic=embedded.3" }, webEnvironment = WebEnvironment.NONE)
@EmbeddedKafka(topics = { "embedded" }, partitions = 1)
@DirtiesContext
class BeanTest {
    @Autowired
    private ListableBeanFactory beanFactory;
    @Autowired
    private PropertyResolver resolver;
    @Autowired
    private KafkaConfig config;

    @Test
    void inbound_01() {
        final var endpointMap = beanFactory.getBeansOfType(InboundEndpoint.class);
        final var registry = beanFactory.getBean(InboundConsumerRegistry.class);

        Assertions.assertEquals(1, endpointMap.size());
        Assertions.assertEquals(true, endpointMap.containsKey("inboundEndpoint-0"));
        Assertions.assertEquals(true, registry.get("inboundEndpoint-0").consumer() instanceof Consumer<String, String>);
    }

    @Test
    void propertyResolver_01() {
        Assertions.assertEquals("embedded.3",
                resolver.apply("#{@'kafka.config-me.ehp246.test.embedded.consumer.bean.AppConfig$KafkaConfig'.topic}"));
    }

    @Test
    void propertyResolver_02() {
        Assertions.assertThrows(PlaceholderResolutionException.class, () -> resolver.apply("${not.there}"));
    }

    @Test
    void propertyResolver_03() {
        Assertions.assertEquals("embedded.3", resolver.apply("${kafka.config.topic}"));
    }

    @Test
    void propertyResolver_04() {
        Assertions.assertEquals("prefix-embedded.3-suffix", resolver.apply(
                "#{'prefix-' + @'kafka.config-me.ehp246.test.embedded.consumer.bean.AppConfig$KafkaConfig'.topic + '-suffix'}"));
    }

    @Test
    void propertyResolver_05() {
        Assertions.assertEquals("prefix-embedded.3-suffix", resolver.apply("prefix-${kafka.config.topic}-suffix"));
    }
}
