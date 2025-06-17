package me.ehp246.test.embedded.producerfn.value;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

import me.ehp246.aufkafka.api.producer.ProducerFnProvider;
import me.ehp246.aufkafka.api.serializer.jackson.FromJson;
import me.ehp246.aufkafka.api.serializer.jackson.TypeOfJson;
import me.ehp246.aufkafka.core.reflection.ParameterizedTypeBuilder;
import me.ehp246.test.mock.EmbeddedKafkaConfig;
import me.ehp246.test.mock.OutboundEventRecord;

/**
 * @author Lei Yang
 */
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class, MsgListener.class })
@EmbeddedKafka(topics = { AppConfig.TOPIC })
class ValueTest {
    @Autowired
    private ProducerFnProvider fnProvider;

    @Autowired
    private MsgListener listener;

    @Autowired
    private FromJson fromJson;

    @SuppressWarnings("unchecked")
    @Test
    void test_01() {
        final var typeOf = TypeOfJson.of(ParameterizedTypeBuilder.of(List.class, PersonName.class));
        final var expected = List.of(new PersonName(UUID.randomUUID().toString(), UUID.randomUUID().toString()));

        fnProvider.get("").send(OutboundEventRecord.withValueAndType(AppConfig.TOPIC, expected, typeOf));

        final var received = (List<PersonName>) fromJson.fromJson(listener.take().value(), typeOf);

        Assertions.assertEquals(expected.get(0), received.get(0));
    }
}
