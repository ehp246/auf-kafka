package me.ehp246.test.app.server.bean;

import org.apache.kafka.common.Uuid;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufkafka.api.consumer.InboundConsumer;

/**
 * @author Lei Yang
 *
 */
class BeanTest {
    @Test
    void inbound_01() {
        final var appCtx = new AnnotationConfigApplicationContext(AppConfig.Case01.class);

        final var inboundConsumers = appCtx.getBeansOfType(InboundConsumer.class);

        Assertions.assertEquals(1, inboundConsumers.size());
        Assertions.assertEquals(true, inboundConsumers.get("InboundConsumer-0") != null);

        appCtx.close();
    }

    @Test
    void inbound_02() {
        final var topic2 = Uuid.randomUuid().toString();
        final var appCtx = new AnnotationConfigApplicationContext();
        appCtx.setEnvironment(new MockEnvironment().withProperty("topic2", topic2));
        appCtx.register(AppConfig.Case02.class);
        appCtx.refresh();

        final var inboundConsumers = appCtx.getBeansOfType(InboundConsumer.class);

        Assertions.assertEquals(2, inboundConsumers.size());
        Assertions.assertEquals(true, inboundConsumers.get("topic1.consumer") != null);

        final var inboundConsumer2 = inboundConsumers.get("InboundConsumer-1");

        Assertions.assertEquals(true, inboundConsumer2 != null);
        Assertions.assertEquals(topic2, inboundConsumer2.inboundEndpoint().from().topic());

        appCtx.close();
    }
}
