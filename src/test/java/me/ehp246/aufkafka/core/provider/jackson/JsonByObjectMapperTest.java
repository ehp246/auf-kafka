package me.ehp246.aufkafka.core.provider.jackson;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufkafka.api.serializer.jackson.ObjectOfJson;
import me.ehp246.aufkafka.api.serializer.jackson.TypeOfJson;
import me.ehp246.aufkafka.core.reflection.ParameterizedTypeBuilder;
import me.ehp246.test.TestUtil;

class JsonByObjectMapperTest {
    private final JsonByJackson mapper = new JsonByJackson(TestUtil.OBJECT_MAPPER);

    @Test
    void test_01() {
        final var expected = Instant.now();

        Assertions.assertEquals(mapper.toJson(ObjectOfJson.of(expected)), mapper.toJson(expected));
    }

    @Test
    void test_02() {
        final var expected = Instant.now();

        Assertions.assertEquals(true,
                mapper.fromJson(mapper.toJson(expected), TypeOfJson.of(Instant.class, null)).equals(expected));
    }

    @SuppressWarnings("unchecked")
    @Test
    void test_03() {
        final var typeOf = TypeOfJson.of(ParameterizedTypeBuilder.of(List.class, Instant.class));
        final var expected = List.of(Instant.now());

        final var actual = mapper.fromJson(mapper.toJson(new ObjectOfJson() {

            @Override
            public Object value() {
                return expected;
            }

            @Override
            public TypeOfJson typeOf() {
                return typeOf;
            }

        }), typeOf);

        Assertions.assertEquals(true, actual instanceof List);
        Assertions.assertEquals(expected.get(0), ((List<Instant>) actual).get(0));
    }
}
