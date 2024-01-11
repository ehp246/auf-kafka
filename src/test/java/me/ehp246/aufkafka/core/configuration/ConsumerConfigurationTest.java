package me.ehp246.aufkafka.core.configuration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufkafka.api.AufKafkaConstant;
import me.ehp246.aufkafka.api.consumer.LoggingConsumer;
import me.ehp246.test.mock.MockConsumerConfigProvider;

/**
 * @author Lei Yang
 *
 */
class ConsumerConfigurationTest {

    @Test
    void loggingconsumer_01() {
        final var appCtx = new AnnotationConfigApplicationContext();
        appCtx.register(ConsumerConfiguration.class, MockConsumerConfigProvider.class);
        appCtx.setEnvironment(new MockEnvironment()
                .withProperty("me.ehp246.aufkafka.consumer.messagelogging.enabled", "true"));
        appCtx.refresh();

        Assertions.assertEquals(true, appCtx.getBean(AufKafkaConstant.BEAN_LOGING_CONSUMER,
                LoggingConsumer.class) != null);

        appCtx.close();
    }

    @Test
    void loggingconsumer_02() {
        final var appCtx = new AnnotationConfigApplicationContext();
        appCtx.register(ConsumerConfiguration.class, MockConsumerConfigProvider.class);
        appCtx.refresh();

        Assertions.assertThrows(Exception.class,
                () -> appCtx.getBean(AufKafkaConstant.BEAN_LOGING_CONSUMER, LoggingConsumer.class));

        appCtx.close();
    }
}
