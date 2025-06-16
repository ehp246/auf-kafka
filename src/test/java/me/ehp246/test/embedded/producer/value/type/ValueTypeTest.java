package me.ehp246.test.embedded.producer.value.type;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import me.ehp246.aufkafka.api.serializer.TypeOfJson;
import me.ehp246.aufkafka.api.serializer.json.FromJson;
import me.ehp246.test.embedded.producer.value.type.TestCases.Payload.Person;
import me.ehp246.test.embedded.producer.value.type.TestCases.Payload.PersonDob;
import me.ehp246.test.embedded.producer.value.type.TestCases.Payload.PersonName;
import me.ehp246.test.mock.EmbeddedKafkaConfig;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class, MsgListener.class })
@EmbeddedKafka(topics = { "embedded" }, partitions = 1)
@DirtiesContext
class ValueTypeTest {
    @Autowired
    private TestCases.Case01 case01;

    @Autowired
    private MsgListener listener;

    @Autowired
    private FromJson fromJson;

    @BeforeEach
    void reset() {
        listener.reset();
    }

    @Test
    void type_01() {
        final var firstName = UUID.randomUUID().toString();
        final var lastName = UUID.randomUUID().toString();
        final var now = Instant.now();

        case01.ping(new Person(firstName, lastName, now));

        Assertions.assertEquals("{\"firstName\":\"" + firstName + "\",\"lastName\":\"" + lastName + "\",\"dob\":\""
                + now.toString() + "\"}", listener.take().value());
    }

    @Test
    void type_02() {
        final var firstName = UUID.randomUUID().toString();
        final var lastName = UUID.randomUUID().toString();

        final var now = Instant.now();
        final var expected = new Person(firstName, lastName, now);
        case01.ping((PersonName) expected);

        final var text = listener.take().value();
        final var actual = (Person) fromJson.fromJson(text, TypeOfJson.newInstance(Person.class));

        Assertions.assertEquals(firstName, actual.firstName());
        Assertions.assertEquals(lastName, actual.lastName());
        Assertions.assertEquals(null, actual.dob());
    }

    @Test
    void type_03() {
        final var now = Instant.now();
        final var expected = new Person(null, null, now);
        case01.ping((PersonDob) expected);

        final var text = listener.take().value();
        final var actual = (Person) fromJson.fromJson(text, TypeOfJson.newInstance(Person.class));

        Assertions.assertEquals(null, actual.firstName());
        Assertions.assertEquals(null, actual.lastName());
        Assertions.assertEquals(now.toString(), actual.dob().toString());
    }
}
