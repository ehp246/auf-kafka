package me.ehp246.test.app.server.bean;

import org.apache.kafka.common.Uuid;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufkafka.api.consumer.InboundConsumer;
import me.ehp246.aufkafka.api.consumer.InboundEndpoint;

/**
 * @author Lei Yang
 *
 */
class BeanTest {
    @Test
    void inboundEndpoint_name_01() {
        final var appCtx = new AnnotationConfigApplicationContext(AppConfig.Case01.class);

        final var inbounds = appCtx.getBeansOfType(InboundEndpoint.class);

        Assertions.assertEquals(1, inbounds.size());
        Assertions.assertEquals(true, inbounds.get("InboundEndpoint-0") != null);

        appCtx.close();
    }

    @Test
    void inboundEndpoint_topic_01() {
        final var topic2 = Uuid.randomUuid().toString();
        final var appCtx = new AnnotationConfigApplicationContext();

        appCtx.setEnvironment(new MockEnvironment().withProperty("topic2", topic2));
        appCtx.register(AppConfig.Case02.class);
        appCtx.refresh();

        final var inbounds = appCtx.getBeansOfType(InboundEndpoint.class);

        Assertions.assertEquals(2, inbounds.size());

        final var inbound1 = inbounds.get("topic1.consumer");

        Assertions.assertEquals(true, inbound1 != null);
        Assertions.assertEquals("topic1", inbound1.from().topic());

        final var inbound2 = inbounds.get("InboundEndpoint-1");

        Assertions.assertEquals(true, inbound2 != null);
        Assertions.assertEquals(topic2, inbound2.from().topic());

        appCtx.close();
    }

    @Test
    void inboundConsumer_01() {
        final var appCtx = new AnnotationConfigApplicationContext(AppConfig.Case01.class);

        final var inbounds = appCtx.getBeansOfType(InboundConsumer.class);

        Assertions.assertEquals(1, inbounds.size());
        Assertions.assertEquals(true, inbounds.get("InboundEndpoint-0") != null);

        appCtx.close();
    }
}
