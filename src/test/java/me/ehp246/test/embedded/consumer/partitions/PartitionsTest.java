package me.ehp246.test.embedded.consumer.partitions;

import java.util.stream.IntStream;

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
        "prop.1=4-7" }, webEnvironment = WebEnvironment.NONE)
@EmbeddedKafka(topics = { AppConfig.TOPIC }, partitions = 1)
class PartitionsTest {
    @Autowired
    private ListableBeanFactory beanFactory;

    @Test
    void partitions_01() {
        Assertions.assertEquals(0,
                beanFactory.getBeansOfType(InboundEndpoint.class).get("inboundEndpoint-0").from().partitions().size());
    }

    @Test
    void partitions_02() {
        final var partitions = beanFactory.getBeansOfType(InboundEndpoint.class).get("inboundEndpoint-1").from()
                .partitions();

        Assertions.assertEquals(11, partitions.size());

        IntStream.range(0, 11)
                .forEach(i -> Assertions.assertEquals(i, partitions.get(i), "should be sorted and de-duped."));
    }
}
