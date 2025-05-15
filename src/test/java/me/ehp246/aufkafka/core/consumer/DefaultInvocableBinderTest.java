package me.ehp246.aufkafka.core.consumer;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufkafka.api.consumer.InboundEvent;
import me.ehp246.aufkafka.api.consumer.Invoked.Completed;
import me.ehp246.aufkafka.api.consumer.Invoked.Failed;
import me.ehp246.aufkafka.api.exception.UnboundParameterException;
import me.ehp246.aufkafka.api.serializer.json.FromJson;
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
    void bound_01() {
        final var method = new ReflectedType<>(InvocableBinderTestCases.ArgCase01.class).findMethod("m01");
        final var arg01 = new InvocableBinderTestCases.ArgCase01();
        final var invocable = new InvocableRecord(arg01, method);
        final var bound = binder.bind(invocable, new InboundEvent(new MockConsumerRecord()));

        Assertions.assertEquals(arg01, bound.invocable().instance());
        Assertions.assertEquals(method, bound.invocable().method());
        Assertions.assertEquals(0, bound.arguments().length);
        Assertions.assertEquals(invocable.invocationModel(), bound.invocable().invocationModel());
    }

    @Test
    void arg_01() {
        final var method = new ReflectedType<>(InvocableBinderTestCases.ArgCase01.class).findMethod("m01",
                ConsumerRecord.class);
        final var msg = new InboundEvent(new MockConsumerRecord());
        final var bound = binder.bind(new InvocableRecord(new InvocableBinderTestCases.ArgCase01(), method), msg);

        Assertions.assertEquals(1, bound.arguments().length);
        Assertions.assertEquals(msg.consumerRecord(), bound.arguments()[0]);
    }

    @Test
    void arg_02() {
        Assertions.assertThrows(UnboundParameterException.class,
                () -> binder.bind(new InvocableRecord(new InvocableBinderTestCases.ArgCase01(),
                        new ReflectedType<>(InvocableBinderTestCases.ArgCase01.class).findMethod("m01", String.class)),
                        new InboundEvent(new MockConsumerRecord())));
        ;
    }

    @Test
    void arg_03() {
        final var event = new InboundEvent(new MockConsumerRecord());
        final var method = new ReflectedType<>(InvocableBinderTestCases.ArgCase01.class).findMethod("m01",
                ConsumerRecord.class, FromJson.class);

        final var bound = binder.bind(new InvocableRecord(new InvocableBinderTestCases.ArgCase01(), method), event);

        Assertions.assertEquals(2, bound.arguments().length);
        Assertions.assertEquals(event.consumerRecord(), bound.arguments()[0]);
        Assertions.assertEquals(jackson, bound.arguments()[1]);
    }

    @SuppressWarnings("unchecked")
    @Test
    void arg_04() {
        final var event = MockConsumerRecord.withValue(jackson.apply(List.of(1, 2, 3), null)).toEvent();
        final var method = new ReflectedType<>(InvocableBinderTestCases.ArgCase01.class).findMethod("m01", List.class,
                ConsumerRecord.class);

        final var bound = binder.bind(new InvocableRecord(new InvocableBinderTestCases.ArgCase01(), method), event);

        Assertions.assertEquals(2, bound.arguments().length);

        final var firstArg = (List<Integer>) bound.arguments()[0];

        Assertions.assertEquals(3, firstArg.size());
        Assertions.assertEquals(1, firstArg.get(0));
        Assertions.assertEquals(2, firstArg.get(1));
        Assertions.assertEquals(3, firstArg.get(2));

        Assertions.assertEquals(event.consumerRecord(), bound.arguments()[1]);
    }

    @Test
    public void exception_01() throws Exception {
        final var outcome = binder.bind(
                new InvocableRecord(new InvocableBinderTestCases.ExceptionCase01(),
                        ReflectedType.reflect(InvocableBinderTestCases.ExceptionCase01.class).findMethod("m01")),
                new InboundEvent(new MockConsumerRecord())).invoke();

        Assertions.assertEquals(IllegalArgumentException.class, ((Failed) outcome).thrown().getClass());
    }

    @Test
    void key_01() {
        final var value = UUID.randomUUID().toString();
        final var case01 = new InvocableBinderTestCases.KeyCase01();

        final var event = MockConsumerRecord.withValue(jackson.apply(value)).toEvent();
        final var outcome = binder
                .bind(new InvocableRecord(case01, new ReflectedType<>(InvocableBinderTestCases.KeyCase01.class)
                        .findMethod("m01", ConsumerRecord.class, String.class, String.class)), event)
                .invoke();

        final var returned = (Object[]) ((Completed) outcome).returned();

        Assertions.assertEquals(true, returned[0] == event.consumerRecord());
        Assertions.assertEquals(true, returned[1].equals(event.key()));
        Assertions.assertEquals(true, returned[2].equals(value));
    }

    @Test
    void value_01() {
        final var bound = binder.bind(
                new InvocableRecord(new InvocableBinderTestCases.ValueCase01(), ReflectedType
                        .reflect(InvocableBinderTestCases.ValueCase01.class).findMethod("m01", Received.class)),
                new InboundEvent(new MockConsumerRecord()));

        Assertions.assertEquals(true, ((Completed) bound.invoke()).returned() == null);
        Assertions.assertEquals(1, bound.arguments().length);
        Assertions.assertEquals(null, bound.arguments()[0]);
    }

    @Test
    void value_02() {
        final var expected = new Account(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        final var bound = binder.bind(
                new InvocableRecord(new InvocableBinderTestCases.ValueCase01(), ReflectedType
                        .reflect(InvocableBinderTestCases.ValueCase01.class).findMethod("m01", Received.class)),
                MockConsumerRecord.withValue(jackson.apply(expected)).toEvent());

        final var returned = (Received) (((Completed) bound.invoke()).returned());

        Assertions.assertEquals(expected.id(), returned.getId());
        Assertions.assertEquals(expected.password(), returned.getPassword());
    }

    @Test
    void value_03() {
        final var expected = new Account(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        final var bound = binder.bind(
                new InvocableRecord(new InvocableBinderTestCases.ValueCase01(), ReflectedType
                        .reflect(InvocableBinderTestCases.ValueCase01.class).findMethod("m02", Received.class)),
                MockConsumerRecord.withValue(jackson.apply(expected)).toEvent());

        final var returned = (Received) (((Completed) bound.invoke()).returned());

        Assertions.assertEquals(expected.id(), returned.getId());
        Assertions.assertEquals(null, returned.getPassword());
    }

    @Test
    void value_04() {
        final var expected = new Account(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        final var bound = binder.bind(
                new InvocableRecord(new InvocableBinderTestCases.ValueCase01(), ReflectedType
                        .reflect(InvocableBinderTestCases.ValueCase01.class).findMethod("m03", Received.class)),
                MockConsumerRecord.withValue(jackson.apply(expected)).toEvent());

        final var returned = (Received) (((Completed) bound.invoke()).returned());

        Assertions.assertEquals(expected.id(), returned.getId());
        Assertions.assertEquals(expected.password(), returned.getPassword());
    }

    @Test
    void header_01() {
        final var bound = binder.bind(
                new InvocableRecord(new InvocableBinderTestCases.HeaderCase01(), ReflectedType
                        .reflect(InvocableBinderTestCases.HeaderCase01.class).findMethod("m01", String.class)),
                new InboundEvent(new MockConsumerRecord()));

        Assertions.assertEquals(true, ((Completed) bound.invoke()).returned() == null);
        Assertions.assertEquals(1, bound.arguments().length);
        Assertions.assertEquals(null, bound.arguments()[0]);
    }

    @Test
    void header_02() {
        final var map = Map.of("prop1", UUID.randomUUID().toString());
        final var mq = MockConsumerRecord.withHeaders(StringHeader.headers(map)).toEvent();
        final var bound = binder.bind(new InvocableRecord(new InvocableBinderTestCases.HeaderCase01(),
                new ReflectedType<>(InvocableBinderTestCases.HeaderCase01.class).findMethod("m01", String.class,
                        String.class)),
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
        final var map = Map.of("prop1", UUID.randomUUID().toString(), "prop2", UUID.randomUUID().toString());
        final var mq = MockConsumerRecord.withHeaders(StringHeader.headers(map)).toEvent();

        final var bound = binder.bind(new InvocableRecord(new InvocableBinderTestCases.HeaderCase01(),
                new ReflectedType<>(InvocableBinderTestCases.HeaderCase01.class).findMethod("m01", String.class,
                        String.class)),
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
        final var mq = MockConsumerRecord.withHeaders(StringHeader.headers("Prop1", "true")).toEvent();
        final var bound = binder.bind(new InvocableRecord(new InvocableBinderTestCases.HeaderCase01(),
                new ReflectedType<>(InvocableBinderTestCases.HeaderCase01.class).findMethod("m01", Boolean.class)), mq);
        final var outcome = bound.invoke();

        final var returned = (Boolean) ((Completed) outcome).returned();

        Assertions.assertEquals(true, returned);

        Assertions.assertEquals(1, bound.arguments().length);
        Assertions.assertEquals(true, bound.arguments()[0]);
    }

    @Test
    void header_06() {
        final var mq = new InboundEvent(new MockConsumerRecord());
        final var bound = binder.bind(new InvocableRecord(new InvocableBinderTestCases.HeaderCase01(),
                new ReflectedType<>(InvocableBinderTestCases.HeaderCase01.class).findMethod("m01", Boolean.class)), mq);
        final var outcome = bound.invoke();

        final var returned = (Boolean) ((Completed) outcome).returned();

        Assertions.assertEquals(null, returned);

        Assertions.assertEquals(1, bound.arguments().length);
        Assertions.assertEquals(null, bound.arguments()[0]);
    }

    @Test
    void header_07() {
        final var mq = MockConsumerRecord.withHeaders(StringHeader.headers("prop1", PropertyEnum.Enum1.toString()))
                .toEvent();
        final var bound = binder.bind(new InvocableRecord(new InvocableBinderTestCases.HeaderCase01(),
                new ReflectedType<>(InvocableBinderTestCases.HeaderCase01.class).findMethod("m01", PropertyEnum.class)),
                mq);
        final var outcome = bound.invoke();

        Assertions.assertEquals(PropertyEnum.Enum1, ((Completed) outcome).returned());

        Assertions.assertEquals(1, bound.arguments().length);
        Assertions.assertEquals(PropertyEnum.Enum1, bound.arguments()[0]);
    }

    @SuppressWarnings("unchecked")
    @Test
    void header_08() {
        final var mq = MockConsumerRecord.withHeaders(StringHeader.headers("prop1", PropertyEnum.Enum1.toString()))
                .toEvent();
        final var bound = binder.bind(
                new InvocableRecord(new InvocableBinderTestCases.HeaderCase01(),
                        new ReflectedType<>(InvocableBinderTestCases.HeaderCase01.class).findMethod("m01", Map.class)),
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
        final var mq = MockConsumerRecord.withHeaders(StringHeader.headers("prop1", UUID.randomUUID().toString()))
                .toEvent();
        final var bound = binder.bind(new InvocableRecord(new InvocableBinderTestCases.HeaderCase01(),
                new ReflectedType<>(InvocableBinderTestCases.HeaderCase01.class).findMethod("m01", Headers.class)), mq);
        final var outcome = bound.invoke();
        final var returned = (Object[]) ((Completed) outcome).returned();

        Assertions.assertEquals(1, returned.length);

        Assertions.assertEquals(1, bound.arguments().length);

        Assertions.assertEquals(true, mq.headers() == (Headers) bound.arguments()[0]);
    }

    @Test
    void mdc_01() {
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MDCCase(),
                new ReflectedType<>(InvocableBinderTestCases.MDCCase.class).findMethod("get"));

        final var bound = binder.bind(invocable, new InboundEvent(new MockConsumerRecord()));

        Assertions.assertEquals(0, bound.mdcMap().size());
    }

    @Test
    void mdc_02() {
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MDCCase(),
                new ReflectedType<>(InvocableBinderTestCases.MDCCase.class).findMethod("get", String.class,
                        String.class));

        final var bound = binder.bind(invocable, new InboundEvent(new MockConsumerRecord()));

        Assertions.assertEquals(1, bound.mdcMap().size());
        Assertions.assertEquals(null, bound.mdcMap().get("name"));
    }

    @Test
    void mdc_03() {
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MDCCase(),
                new ReflectedType<>(InvocableBinderTestCases.MDCCase.class).findMethod("get", String.class,
                        String.class));
        final var lastName = UUID.randomUUID().toString();
        final var bound = binder.bind(invocable, MockConsumerRecord.withHeaders("LastName", lastName).toEvent());

        Assertions.assertEquals(1, bound.mdcMap().size());
        Assertions.assertEquals(lastName, bound.mdcMap().get("name"), "should take the last one");
    }

    @Test
    void mdc_04() {
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MDCCase(),
                new ReflectedType<>(InvocableBinderTestCases.MDCCase.class).findMethod("get", String.class, int.class));
        final var expected = UUID.randomUUID().toString();
        final var bound = binder.bind(invocable,
                new InboundEvent(new MockConsumerRecord(null, jackson.apply(expected), "Id", "123")));

        Assertions.assertEquals(2, bound.mdcMap().size());
        Assertions.assertEquals(expected, bound.mdcMap().get("name"));
        Assertions.assertEquals("123", bound.mdcMap().get("SSN"));
    }

    @Test
    void mdc_05() {
        final var method = new ReflectedType<>(InvocableBinderTestCases.MDCCase.class).findMethod("get", String.class,
                Integer.class);
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MDCCase(), method);
        final var expected = UUID.randomUUID().toString();
        final var bound = binder.bind(invocable, MockConsumerRecord.withValue(jackson.apply(expected)).toEvent());

        Assertions.assertEquals(2, bound.mdcMap().size());
        Assertions.assertEquals(expected, bound.mdcMap().get("name"));
        Assertions.assertEquals(null, bound.mdcMap().get("SSN"));
    }

    @Test
    void mdc_06() {
        final var method = new ReflectedType<>(InvocableBinderTestCases.MDCCase.class).findMethod("getOnBody",
                InvocableBinderTestCases.MDCCase.Name.class);
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MDCCase(), method);
        final var expected = new InvocableBinderTestCases.MDCCase.Name(UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
        final var bound = binder.bind(invocable, MockConsumerRecord.withValue(jackson.apply(expected)).toEvent());

        Assertions.assertEquals(1, bound.mdcMap().size());
        Assertions.assertEquals(expected.toString(), bound.mdcMap().get("name"), "should take all annotated");
    }

    @Test
    void mdc_06_01() {
        final var method = new ReflectedType<>(InvocableBinderTestCases.MDCCase.class).findMethod("getOnBody",
                InvocableBinderTestCases.MDCCase.Name.class);
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MDCCase(), method);
        final var bound = binder.bind(invocable, MockConsumerRecord.withValue(jackson.apply(null)).toEvent());

        Assertions.assertEquals(1, bound.mdcMap().size());
        Assertions.assertEquals(null, bound.mdcMap().get("name"), "should tolerate null");
    }

    @Test
    void mdc_07() {
        final var method = new ReflectedType<>(InvocableBinderTestCases.MDCCase.class).findMethod("getInBody",
                InvocableBinderTestCases.MDCCase.Name.class);
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MDCCase(), method);
        final var expected = new InvocableBinderTestCases.MDCCase.Name(UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
        final var bound = binder.bind(invocable, MockConsumerRecord.withValue(jackson.apply(expected)).toEvent());

        Assertions.assertEquals(0, bound.mdcMap().size(), "should not have any without annotation");
    }

    @Test
    void mdc_08() {
        final var method = new ReflectedType<>(InvocableBinderTestCases.MDCCase.class).findMethod("getInBody",
                InvocableBinderTestCases.MDCCase.Name.class);
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MDCCase(), method);
        final var expected = new InvocableBinderTestCases.MDCCase.Name(UUID.randomUUID().toString(), null);
        final var bound = binder.bind(invocable, MockConsumerRecord.withValue(jackson.apply(expected)).toEvent());

        Assertions.assertEquals(0, bound.mdcMap().size());
    }

    @Test
    void mdc_09() {
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MDCCase(),
                new ReflectedType<>(InvocableBinderTestCases.MDCCase.class).findMethod("getOnBodyIntro",
                        InvocableBinderTestCases.MDCCase.Name.class));

        final var name = new InvocableBinderTestCases.MDCCase.Name(UUID.randomUUID().toString(),
                UUID.randomUUID().toString());

        final var mdcMap = binder.bind(invocable, MockConsumerRecord.withValue(jackson.apply(name)).toEvent()).mdcMap();

        Assertions.assertEquals(3, mdcMap.size());
        Assertions.assertEquals(name.firstName(), mdcMap.get("firstName"));
        Assertions.assertEquals(name.lastName(), mdcMap.get("lastName"));
        Assertions.assertEquals(name.fullName(), mdcMap.get("fullName"));
    }

    @Test
    void mdc_09_01() {
        final var method = new ReflectedType<>(InvocableBinderTestCases.MDCCase.class).findMethod("getOnBodyIntro",
                InvocableBinderTestCases.MDCCase.Name.class);
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MDCCase(), method);

        final var mdcMap = binder.bind(invocable, new InboundEvent(new MockConsumerRecord())).mdcMap();

        Assertions.assertEquals(3, mdcMap.size());
        Assertions.assertEquals(null, mdcMap.get("firstName"));
        Assertions.assertEquals(null, mdcMap.get("lastName"));
        Assertions.assertEquals(null, mdcMap.get("fullName"));
    }

    @Test
    void mdc_09_02() {
        final var method = new ReflectedType<>(InvocableBinderTestCases.MDCCase.class).findMethod("getOnBodyIntro",
                InvocableBinderTestCases.MDCCase.Name.class);
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MDCCase(), method);

        final var name = new InvocableBinderTestCases.MDCCase.Name(UUID.randomUUID().toString(), null);

        final var mdcMap = binder.bind(invocable, MockConsumerRecord.withValue(jackson.apply(name)).toEvent()).mdcMap();

        Assertions.assertEquals(3, mdcMap.size());
        Assertions.assertEquals(name.firstName(), mdcMap.get("firstName"));
        Assertions.assertEquals(name.lastName(), mdcMap.get("lastName"));
        Assertions.assertEquals(name.fullName(), mdcMap.get("fullName"));
    }

    @Test
    void mdc_10() {
        final var method = new ReflectedType<>(InvocableBinderTestCases.MDCCase.class).findMethod("getOnBodyPrec",
                InvocableBinderTestCases.MDCCase.Name.class, String.class);
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MDCCase(), method);

        final var name = new InvocableBinderTestCases.MDCCase.Name(UUID.randomUUID().toString(),
                UUID.randomUUID().toString());

        final var mdcMap = binder.bind(invocable, MockConsumerRecord.withValue(jackson.apply(name)).toEvent()).mdcMap();

        Assertions.assertEquals(3, mdcMap.size());
        Assertions.assertEquals(name.firstName(), mdcMap.get("firstName"), "should follow the body");
        Assertions.assertEquals(name.lastName(), mdcMap.get("lastName"));
        Assertions.assertEquals(name.fullName(), mdcMap.get("fullName"));
    }

    @Test
    void mdc_11() {
        final var method = new ReflectedType<>(InvocableBinderTestCases.MDCCase.class).findMethod("getOnBodyIntroNamed",
                InvocableBinderTestCases.MDCCase.Name.class);
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MDCCase(), method);

        final var name = new InvocableBinderTestCases.MDCCase.Name(UUID.randomUUID().toString(),
                UUID.randomUUID().toString());

        final var mdcMap = binder.bind(invocable, MockConsumerRecord.withValue(jackson.apply(name)).toEvent()).mdcMap();

        Assertions.assertEquals(3, mdcMap.size());
        Assertions.assertEquals(name.firstName(), mdcMap.get("Name.firstName"));
        Assertions.assertEquals(name.lastName(), mdcMap.get("Name.lastName"));
        Assertions.assertEquals(name.fullName(), mdcMap.get("Name.fullName"));
    }

    @Test
    void mdc_12() {
        final var method = new ReflectedType<>(InvocableBinderTestCases.MDCCase.class).findMethod("getOnBodyNamed",
                InvocableBinderTestCases.MDCCase.Name.class, String.class);

        final var name = new InvocableBinderTestCases.MDCCase.Name(UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
        final var firstName = UUID.randomUUID().toString();

        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MDCCase(), method);

        final var mdcMap = binder
                .bind(invocable, new MockConsumerRecord(null, jackson.apply(name), "FirstName", firstName).toEvent())
                .mdcMap();

        Assertions.assertEquals(2, mdcMap.size());

        Assertions.assertEquals(name.toString(), mdcMap.get("newName"));
        Assertions.assertEquals(firstName, mdcMap.get("firstName"));
    }
}
