package me.ehp246.aufkafka.core.consumer;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.kafka.common.header.Headers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufkafka.api.consumer.Invoked.Completed;
import me.ehp246.aufkafka.api.serializer.json.FromJson;
import me.ehp246.aufkafka.api.serializer.json.ToJson;
import me.ehp246.aufkafka.core.consumer.InvocableBinderTestCases.HeaderCase01.PropertyEnum;
import me.ehp246.aufkafka.core.provider.jackson.JsonByObjectMapper;
import me.ehp246.aufkafka.core.reflection.ReflectedType;
import me.ehp246.test.TestUtil;
import me.ehp246.test.mock.InvocableRecord;
import me.ehp246.test.mock.MockConsumerRecord;
import me.ehp246.test.mock.StringHeader;

/**
 * @author Lei Yang
 *
 */
class DefaultInvocableBinderTest {
    private final JsonByObjectMapper jackson = new JsonByObjectMapper(TestUtil.OBJECT_MAPPER);
    private final FromJson fromJson = jackson;
    private final ToJson toJson = jackson;
    private final DefaultInvocableBinder binder = new DefaultInvocableBinder(fromJson);

    @Test
    void header_01() {
        final var bound = binder
                .bind(new InvocableRecord(new InvocableBinderTestCases.HeaderCase01(),
                        ReflectedType.reflect(InvocableBinderTestCases.HeaderCase01.class)
                                .findMethod("m01", String.class)),
                        new MockConsumerRecord());

        Assertions.assertEquals(true, ((Completed) bound.invoke()).returned() == null);
        Assertions.assertEquals(1, bound.arguments().length);
        Assertions.assertEquals(null, bound.arguments()[0]);
    }

    @Test
    void header_02() {
        final var map = Map.of("prop1", UUID.randomUUID().toString());
        final var mq = MockConsumerRecord.withHeaders(StringHeader.headers(map));
        final var bound = binder
                .bind(new InvocableRecord(new InvocableBinderTestCases.HeaderCase01(),
                        new ReflectedType<>(InvocableBinderTestCases.HeaderCase01.class)
                                .findMethod("m01", String.class, String.class)),
                        mq);

        final var outcome = bound.invoke();

        final var returned = (String[]) ((Completed) outcome).returned();

        Assertions.assertEquals(map.get("prop1"), returned[0]);
        Assertions.assertEquals(null, returned[1]);

        Assertions.assertEquals(2, bound.arguments().length);
        Assertions.assertEquals(map.get("prop1"), bound.arguments()[0]);
        Assertions.assertEquals(null, bound.arguments()[1]);
    }

    @Test
    void header_03() {
        final var map = Map.of("prop1", UUID.randomUUID().toString(), "prop2",
                UUID.randomUUID().toString());
        final var mq = MockConsumerRecord.withHeaders(StringHeader.headers(map));

        final var bound = binder
                .bind(new InvocableRecord(new InvocableBinderTestCases.HeaderCase01(),
                        new ReflectedType<>(InvocableBinderTestCases.HeaderCase01.class)
                                .findMethod("m01", String.class, String.class)),
                        mq);
        final var outcome = bound.invoke();

        final var returned = (String[]) ((Completed) outcome).returned();

        Assertions.assertEquals(map.get("prop1"), returned[0]);
        Assertions.assertEquals(map.get("prop2"), returned[1]);

        Assertions.assertEquals(2, bound.arguments().length);

        Assertions.assertEquals(map.get("prop1"), bound.arguments()[0]);
        Assertions.assertEquals(map.get("prop2"), bound.arguments()[1]);
    }

    @Test
    void header_05() {
        final var mq = MockConsumerRecord.withHeaders(StringHeader.headers("Prop1", "true"));
        final var bound = binder
                .bind(new InvocableRecord(new InvocableBinderTestCases.HeaderCase01(),
                        new ReflectedType<>(InvocableBinderTestCases.HeaderCase01.class)
                                .findMethod("m01", Boolean.class)),
                        mq);
        final var outcome = bound.invoke();

        final var returned = (Boolean) ((Completed) outcome).returned();

        Assertions.assertEquals(true, returned);

        Assertions.assertEquals(1, bound.arguments().length);
        Assertions.assertEquals(true, bound.arguments()[0]);
    }

    @Test
    void header_06() {
        final var mq = new MockConsumerRecord();
        final var bound = binder
                .bind(new InvocableRecord(new InvocableBinderTestCases.HeaderCase01(),
                        new ReflectedType<>(InvocableBinderTestCases.HeaderCase01.class)
                                .findMethod("m01", Boolean.class)),
                        mq);
        final var outcome = bound.invoke();

        final var returned = (Boolean) ((Completed) outcome).returned();

        Assertions.assertEquals(null, returned);

        Assertions.assertEquals(1, bound.arguments().length);
        Assertions.assertEquals(null, bound.arguments()[0]);
    }

    @Test
    void header_07() {
        final var mq = MockConsumerRecord
                .withHeaders(StringHeader.headers("prop1", PropertyEnum.Enum1.toString()));
        final var bound = binder
                .bind(new InvocableRecord(new InvocableBinderTestCases.HeaderCase01(),
                        new ReflectedType<>(InvocableBinderTestCases.HeaderCase01.class)
                                .findMethod("m01", PropertyEnum.class)),
                        mq);
        final var outcome = bound.invoke();

        Assertions.assertEquals(PropertyEnum.Enum1, ((Completed) outcome).returned());

        Assertions.assertEquals(1, bound.arguments().length);
        Assertions.assertEquals(PropertyEnum.Enum1, bound.arguments()[0]);
    }

    @SuppressWarnings("unchecked")
    @Test
    void header_08() {
        final var mq = MockConsumerRecord
                .withHeaders(StringHeader.headers("prop1", PropertyEnum.Enum1.toString()));
        final var bound = binder
                .bind(new InvocableRecord(new InvocableBinderTestCases.HeaderCase01(),
                        new ReflectedType<>(InvocableBinderTestCases.HeaderCase01.class)
                                .findMethod("m01", Map.class)),
                        mq);
        final var outcome = bound.invoke();
        final var returned = (Object[]) ((Completed) outcome).returned();

        Assertions.assertEquals(1, returned.length);

        Assertions.assertEquals(1, bound.arguments().length);

        final var map = (Map<String, List<String>>) bound.arguments()[0];

        Assertions.assertEquals(1, map.size());
        Assertions.assertEquals(PropertyEnum.Enum1.toString(), map.get("prop1").get(0));
    }

    @Test
    void header_09() {
        final var mq = MockConsumerRecord
                .withHeaders(StringHeader.headers("prop1", UUID.randomUUID().toString()));
        final var bound = binder
                .bind(new InvocableRecord(new InvocableBinderTestCases.HeaderCase01(),
                        new ReflectedType<>(InvocableBinderTestCases.HeaderCase01.class)
                                .findMethod("m01", Headers.class)),
                        mq);
        final var outcome = bound.invoke();
        final var returned = (Object[]) ((Completed) outcome).returned();

        Assertions.assertEquals(1, returned.length);

        Assertions.assertEquals(1, bound.arguments().length);

        Assertions.assertEquals(true, mq.headers() == (Headers) bound.arguments()[0]);
    }
}
