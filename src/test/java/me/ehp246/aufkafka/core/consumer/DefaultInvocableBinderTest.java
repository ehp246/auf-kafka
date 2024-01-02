package me.ehp246.aufkafka.core.consumer;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.kafka.common.header.Headers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufkafka.api.consumer.Invoked.Completed;
import me.ehp246.aufkafka.core.consumer.InvocableBinderTestCases.HeaderCase01.PropertyEnum;
import me.ehp246.aufkafka.core.consumer.InvocableBinderTestCases.ValueCase01.Account;
import me.ehp246.aufkafka.core.consumer.InvocableBinderTestCases.ValueCase01.Received;
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
    private final DefaultInvocableBinder binder = new DefaultInvocableBinder(jackson);

    @Test
    void value_01() {
        final var bound = binder
                .bind(new InvocableRecord(new InvocableBinderTestCases.ValueCase01(),
                        ReflectedType.reflect(InvocableBinderTestCases.ValueCase01.class)
                                .findMethod("m01", Received.class)),
                        new MockConsumerRecord());

        Assertions.assertEquals(true, ((Completed) bound.invoke()).returned() == null);
        Assertions.assertEquals(1, bound.arguments().length);
        Assertions.assertEquals(null, bound.arguments()[0]);
    }

    @Test
    void value_02() {
        final var expected = new Account(UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
        final var bound = binder.bind(
                new InvocableRecord(new InvocableBinderTestCases.ValueCase01(),
                        ReflectedType.reflect(InvocableBinderTestCases.ValueCase01.class)
                                .findMethod("m01", Received.class)),
                MockConsumerRecord.withValue(jackson.apply(expected)));

        final var returned = (Received) (((Completed) bound.invoke()).returned());

        Assertions.assertEquals(expected.id(), returned.getId());
        Assertions.assertEquals(expected.password(), returned.getPassword());
    }

    @Test
    void value_03() {
        final var expected = new Account(UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
        final var bound = binder.bind(
                new InvocableRecord(new InvocableBinderTestCases.ValueCase01(),
                        ReflectedType.reflect(InvocableBinderTestCases.ValueCase01.class)
                                .findMethod("m02", Received.class)),
                MockConsumerRecord.withValue(jackson.apply(expected)));

        final var returned = (Received) (((Completed) bound.invoke()).returned());

        Assertions.assertEquals(expected.id(), returned.getId());
        Assertions.assertEquals(null, returned.getPassword());
    }

    @Test
    void value_04() {
        final var expected = new Account(UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
        final var bound = binder.bind(
                new InvocableRecord(new InvocableBinderTestCases.ValueCase01(),
                        ReflectedType.reflect(InvocableBinderTestCases.ValueCase01.class)
                                .findMethod("m03", Received.class)),
                MockConsumerRecord.withValue(jackson.apply(expected)));

        final var returned = (Received) (((Completed) bound.invoke()).returned());

        Assertions.assertEquals(expected.id(), returned.getId());
        Assertions.assertEquals(expected.password(), returned.getPassword());
    }

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

    @Test
    void log4jContext_01() {
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.Log4jContextCase(),
                new ReflectedType<>(InvocableBinderTestCases.Log4jContextCase.class)
                        .findMethod("get"));

        final var bound = binder.bind(invocable, new MockConsumerRecord());

        Assertions.assertEquals(0, bound.log4jContext().size());
    }

    @Test
    void log4jContext_02() {
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.Log4jContextCase(),
                new ReflectedType<>(InvocableBinderTestCases.Log4jContextCase.class)
                        .findMethod("get", String.class, String.class));

        final var bound = binder.bind(invocable, new MockConsumerRecord());

        Assertions.assertEquals(1, bound.log4jContext().size());
        Assertions.assertEquals(null, bound.log4jContext().get("name"));
    }

    @Test
    void log4jContext_03() {
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.Log4jContextCase(),
                new ReflectedType<>(InvocableBinderTestCases.Log4jContextCase.class)
                        .findMethod("get", String.class, String.class));
        final var lastName = UUID.randomUUID().toString();
        final var bound = binder.bind(invocable,
                MockConsumerRecord.withHeaders("LastName", lastName));

        Assertions.assertEquals(1, bound.log4jContext().size());
        Assertions.assertEquals(lastName, bound.log4jContext().get("name"),
                "should take the last one");
    }

    @Test
    void log4jContext_04() {
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.Log4jContextCase(),
                new ReflectedType<>(InvocableBinderTestCases.Log4jContextCase.class)
                        .findMethod("get", String.class, int.class));
        final var expected = UUID.randomUUID().toString();
        final var bound = binder.bind(invocable,
                new MockConsumerRecord(jackson.apply(expected), "Id", "123"));

        Assertions.assertEquals(2, bound.log4jContext().size());
        Assertions.assertEquals(expected, bound.log4jContext().get("name"));
        Assertions.assertEquals("123", bound.log4jContext().get("SSN"));
    }

    @Test
    void log4jContext_05() {
        final var method = new ReflectedType<>(InvocableBinderTestCases.Log4jContextCase.class)
                .findMethod("get", String.class, Integer.class);
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.Log4jContextCase(),
                method);
        final var expected = UUID.randomUUID().toString();
        final var bound = binder.bind(invocable, new MockConsumerRecord(jackson.apply(expected)));

        Assertions.assertEquals(2, bound.log4jContext().size());
        Assertions.assertEquals(expected, bound.log4jContext().get("name"));
        Assertions.assertEquals(null, bound.log4jContext().get("SSN"));
    }

    @Test
    void log4jContext_06() {
        final var method = new ReflectedType<>(InvocableBinderTestCases.Log4jContextCase.class)
                .findMethod("getOnBody", InvocableBinderTestCases.Log4jContextCase.Name.class);
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.Log4jContextCase(),
                method);
        final var expected = new InvocableBinderTestCases.Log4jContextCase.Name(
                UUID.randomUUID().toString(), UUID.randomUUID().toString());
        final var bound = binder.bind(invocable, new MockConsumerRecord(jackson.apply(expected)));

        Assertions.assertEquals(1, bound.log4jContext().size());
        Assertions.assertEquals(expected.toString(), bound.log4jContext().get("name"),
                "should take all annotated");
    }

    @Test
    void log4jContext_06_01() {
        final var method = new ReflectedType<>(InvocableBinderTestCases.Log4jContextCase.class)
                .findMethod("getOnBody", InvocableBinderTestCases.Log4jContextCase.Name.class);
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.Log4jContextCase(),
                method);
        final var bound = binder.bind(invocable, new MockConsumerRecord(jackson.apply(null)));

        Assertions.assertEquals(1, bound.log4jContext().size());
        Assertions.assertEquals(null, bound.log4jContext().get("name"), "should tolerate null");
    }

    @Test
    void log4jContext_07() {
        final var method = new ReflectedType<>(InvocableBinderTestCases.Log4jContextCase.class)
                .findMethod("getInBody", InvocableBinderTestCases.Log4jContextCase.Name.class);
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.Log4jContextCase(),
                method);
        final var expected = new InvocableBinderTestCases.Log4jContextCase.Name(
                UUID.randomUUID().toString(), UUID.randomUUID().toString());
        final var bound = binder.bind(invocable, new MockConsumerRecord(jackson.apply(expected)));

        Assertions.assertEquals(0, bound.log4jContext().size(),
                "should not have any without annotation");
    }

    @Test
    void log4jContext_08() {
        final var method = new ReflectedType<>(InvocableBinderTestCases.Log4jContextCase.class)
                .findMethod("getInBody", InvocableBinderTestCases.Log4jContextCase.Name.class);
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.Log4jContextCase(),
                method);
        final var expected = new InvocableBinderTestCases.Log4jContextCase.Name(
                UUID.randomUUID().toString(), null);
        final var bound = binder.bind(invocable, new MockConsumerRecord(jackson.apply(expected)));

        Assertions.assertEquals(0, bound.log4jContext().size());
    }

    @Test
    void log4jContext_09() {
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.Log4jContextCase(),
                new ReflectedType<>(InvocableBinderTestCases.Log4jContextCase.class).findMethod(
                        "getOnBodyIntro", InvocableBinderTestCases.Log4jContextCase.Name.class));

        final var name = new InvocableBinderTestCases.Log4jContextCase.Name(
                UUID.randomUUID().toString(), UUID.randomUUID().toString());

        final var log4jContext = binder.bind(invocable, new MockConsumerRecord(jackson.apply(name)))
                .log4jContext();

        Assertions.assertEquals(3, log4jContext.size());
        Assertions.assertEquals(name.firstName(), log4jContext.get("firstName"));
        Assertions.assertEquals(name.lastName(), log4jContext.get("lastName"));
        Assertions.assertEquals(name.fullName(), log4jContext.get("fullName"));
    }

    @Test
    void log4jContext_09_01() {
        final var method = new ReflectedType<>(InvocableBinderTestCases.Log4jContextCase.class)
                .findMethod("getOnBodyIntro", InvocableBinderTestCases.Log4jContextCase.Name.class);
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.Log4jContextCase(),
                method);

        final var log4jContext = binder.bind(invocable, new MockConsumerRecord()).log4jContext();

        Assertions.assertEquals(3, log4jContext.size());
        Assertions.assertEquals(null, log4jContext.get("firstName"));
        Assertions.assertEquals(null, log4jContext.get("lastName"));
        Assertions.assertEquals(null, log4jContext.get("fullName"));
    }

    @Test
    void log4jContext_09_02() {
        final var method = new ReflectedType<>(InvocableBinderTestCases.Log4jContextCase.class)
                .findMethod("getOnBodyIntro", InvocableBinderTestCases.Log4jContextCase.Name.class);
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.Log4jContextCase(),
                method);

        final var name = new InvocableBinderTestCases.Log4jContextCase.Name(
                UUID.randomUUID().toString(), null);

        final var log4jContext = binder.bind(invocable, new MockConsumerRecord(jackson.apply(name)))
                .log4jContext();

        Assertions.assertEquals(3, log4jContext.size());
        Assertions.assertEquals(name.firstName(), log4jContext.get("firstName"));
        Assertions.assertEquals(name.lastName(), log4jContext.get("lastName"));
        Assertions.assertEquals(name.fullName(), log4jContext.get("fullName"));
    }

    @Test
    void log4jContext_10() {
        final var method = new ReflectedType<>(InvocableBinderTestCases.Log4jContextCase.class)
                .findMethod("getOnBodyPrec", InvocableBinderTestCases.Log4jContextCase.Name.class,
                        String.class);
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.Log4jContextCase(),
                method);

        final var name = new InvocableBinderTestCases.Log4jContextCase.Name(
                UUID.randomUUID().toString(), UUID.randomUUID().toString());

        final var log4jContext = binder.bind(invocable, new MockConsumerRecord(jackson.apply(name)))
                .log4jContext();

        Assertions.assertEquals(3, log4jContext.size());
        Assertions.assertEquals(name.firstName(), log4jContext.get("firstName"),
                "should follow the body");
        Assertions.assertEquals(name.lastName(), log4jContext.get("lastName"));
        Assertions.assertEquals(name.fullName(), log4jContext.get("fullName"));
    }

    @Test
    void log4jContext_11() {
        final var method = new ReflectedType<>(InvocableBinderTestCases.Log4jContextCase.class)
                .findMethod("getOnBodyIntroNamed",
                        InvocableBinderTestCases.Log4jContextCase.Name.class);
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.Log4jContextCase(),
                method);

        final var name = new InvocableBinderTestCases.Log4jContextCase.Name(
                UUID.randomUUID().toString(), UUID.randomUUID().toString());

        final var log4jContext = binder.bind(invocable, new MockConsumerRecord(jackson.apply(name)))
                .log4jContext();

        Assertions.assertEquals(3, log4jContext.size());
        Assertions.assertEquals(name.firstName(), log4jContext.get("Name.firstName"));
        Assertions.assertEquals(name.lastName(), log4jContext.get("Name.lastName"));
        Assertions.assertEquals(name.fullName(), log4jContext.get("Name.fullName"));
    }

    @Test
    void log4jContext_12() {
        final var method = new ReflectedType<>(InvocableBinderTestCases.Log4jContextCase.class)
                .findMethod("getOnBodyNamed", InvocableBinderTestCases.Log4jContextCase.Name.class,
                        String.class);

        final var name = new InvocableBinderTestCases.Log4jContextCase.Name(
                UUID.randomUUID().toString(), UUID.randomUUID().toString());
        final var firstName = UUID.randomUUID().toString();

        final var invocable = new InvocableRecord(new InvocableBinderTestCases.Log4jContextCase(),
                method);

        final var log4jContext = binder
                .bind(invocable,
                        new MockConsumerRecord(jackson.apply(name), "FirstName", firstName))
                .log4jContext();

        Assertions.assertEquals(2, log4jContext.size());

        Assertions.assertEquals(name.toString(), log4jContext.get("newName"));
        Assertions.assertEquals(firstName, log4jContext.get("firstName"));
    }
}