package me.ehp246.aufkafka.core.consumer;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufkafka.api.consumer.InboundEvent;
import me.ehp246.aufkafka.api.consumer.InboundEventContext;
import me.ehp246.aufkafka.api.consumer.Invoked.Completed;
import me.ehp246.aufkafka.api.consumer.Invoked.Failed;
import me.ehp246.aufkafka.api.exception.UnboundParameterException;
import me.ehp246.aufkafka.api.serializer.jackson.FromJson;
import me.ehp246.aufkafka.core.consumer.InvocableBinderTestCases.HeaderCase01.PropertyEnum;
import me.ehp246.aufkafka.core.consumer.InvocableBinderTestCases.ValueCase01.Account;
import me.ehp246.aufkafka.core.consumer.InvocableBinderTestCases.ValueCase01.Received;
import me.ehp246.aufkafka.core.provider.jackson.JsonByJackson;
import me.ehp246.aufkafka.core.reflection.ReflectedClass;
import me.ehp246.test.TestUtil;
import me.ehp246.test.mock.InvocableRecord;
import me.ehp246.test.mock.MockConsumerRecord;
import me.ehp246.test.mock.StringHeader;

/**
 * @author Lei Yang
 *
 */
class DefaultEventInvocableBinderTest {
    private final JsonByJackson jackson = new JsonByJackson(TestUtil.OBJECT_MAPPER);
    private final DefaultEventInvocableBinder binder = new DefaultEventInvocableBinder(jackson);

    @Test
    void bound_01() {
        final var method = new ReflectedClass<>(InvocableBinderTestCases.TypeCase01.class).findMethod("m01");
        final var arg01 = new InvocableBinderTestCases.TypeCase01();
        final var invocable = new InvocableRecord(arg01, method);
        InboundEvent event = new InboundEvent(new MockConsumerRecord());
        final var bound = binder.bind(invocable, new InboundEventContext(event, null));

        Assertions.assertEquals(arg01, bound.eventInvocable().instance());
        Assertions.assertEquals(method, bound.eventInvocable().method());
        Assertions.assertEquals(0, bound.arguments().length);
        Assertions.assertEquals(invocable.invocationModel(), bound.eventInvocable().invocationModel());
        Assertions.assertEquals(true, bound.eventContext().event() == event);
    }

    @Test
    void typeArg_01() {
        final var method = new ReflectedClass<>(InvocableBinderTestCases.TypeCase01.class).findMethod("m01",
                ConsumerRecord.class);
        final var event = new InboundEvent(new MockConsumerRecord());
        final var bound = binder.bind(new InvocableRecord(new InvocableBinderTestCases.TypeCase01(), method),
                new InboundEventContext(event, null));

        Assertions.assertEquals(1, bound.arguments().length);
        Assertions.assertEquals(event.consumerRecord(), bound.arguments()[0]);
    }

    @Test
    void typeArg_02() {
        Assertions.assertThrows(UnboundParameterException.class,
                () -> binder.bind(
                        new InvocableRecord(new InvocableBinderTestCases.TypeCase01(),
                                new ReflectedClass<>(InvocableBinderTestCases.TypeCase01.class).findMethod("m01",
                                        String.class)),
                        new InboundEventContext(new InboundEvent(new MockConsumerRecord()), null)));
        ;
    }

    @Test
    void typeArg_03() {
        final var event = new InboundEvent(new MockConsumerRecord());
        final var method = new ReflectedClass<>(InvocableBinderTestCases.TypeCase01.class).findMethod("m01",
                ConsumerRecord.class, FromJson.class);

        final var bound = binder.bind(new InvocableRecord(new InvocableBinderTestCases.TypeCase01(), method),
                new InboundEventContext(event, null));

        Assertions.assertEquals(2, bound.arguments().length);
        Assertions.assertEquals(event.consumerRecord(), bound.arguments()[0]);
        Assertions.assertEquals(jackson, bound.arguments()[1]);
    }

    @Test
    void typeArg_04() {
        final var method = new ReflectedClass<>(InvocableBinderTestCases.TypeCase01.class).findMethod("m01",
                Consumer.class);

        final var context = new InboundEventContext(new InboundEvent(new MockConsumerRecord()),
                new MockConsumer<String, String>(OffsetResetStrategy.EARLIEST));
        final var bound = binder.bind(new InvocableRecord(new InvocableBinderTestCases.TypeCase01(), method), context);

        Assertions.assertEquals(1, bound.arguments().length);
        Assertions.assertEquals(context.consumer(), bound.arguments()[0]);
    }

    @SuppressWarnings("unchecked")
    @Test
    void headerType_04() {
        final var event = MockConsumerRecord.withValue(jackson.toJson(List.of(1, 2, 3))).toEvent();
        final var method = new ReflectedClass<>(InvocableBinderTestCases.TypeCase01.class).findMethod("m01", List.class,
                ConsumerRecord.class);

        final var bound = binder.bind(new InvocableRecord(new InvocableBinderTestCases.TypeCase01(), method),
                new InboundEventContext(event, null));

        Assertions.assertEquals(2, bound.arguments().length);

        final var firstArg = (List<Integer>) bound.arguments()[0];

        Assertions.assertEquals(3, firstArg.size());
        Assertions.assertEquals(1, firstArg.get(0));
        Assertions.assertEquals(2, firstArg.get(1));
        Assertions.assertEquals(3, firstArg.get(2));

        Assertions.assertEquals(event.consumerRecord(), bound.arguments()[1]);
    }

    @Test
    void headerType_05() {
        final var event = MockConsumerRecord.withValue(jackson.toJson(List.of(1, 2, 3))).toEvent();
        final var method = new ReflectedClass<>(InvocableBinderTestCases.TypeCase01.class).findMethod("m01",
                InboundEvent.class);

        final var bound = binder.bind(new InvocableRecord(new InvocableBinderTestCases.TypeCase01(), method),
                new InboundEventContext(event, null));

        Assertions.assertEquals(1, bound.arguments().length);
        Assertions.assertEquals(event, bound.arguments()[0]);
    }

    @Test
    void headerType_06() {
        final var event = MockConsumerRecord
                .withHeaders("h1", "v1", "MyHeader", "myHeader.v1", "h1", "v2", "MyHeader", "myHeader.v2").toEvent();
        final var method = new ReflectedClass<>(InvocableBinderTestCases.TypeCase01.class).findMethod("header",
                Headers.class, Header.class);

        final var bound = binder.bind(new InvocableRecord(new InvocableBinderTestCases.TypeCase01(), method),
                new InboundEventContext(event, null));

        Assertions.assertEquals(2, bound.arguments().length);
        Assertions.assertEquals(event.headers(), bound.arguments()[0]);

        final var returned = (Object[]) ((Completed) bound.invoke()).returned();

        Assertions.assertEquals(event.headers(), returned[0]);
        Assertions.assertEquals(true, ((Header) returned[1]).key().equals("MyHeader"));
        Assertions.assertEquals(true, "myHeader.v2".equals(TestUtil.valueString((Header) returned[1])));
    }

    @Test
    void topic_01() {
        final var event = new MockConsumerRecord().toEvent();
        final var method = new ReflectedClass<>(InvocableBinderTestCases.TopicCase01.class).findMethod("topic",
                String.class);

        final var bound = binder.bind(new InvocableRecord(new InvocableBinderTestCases.TopicCase01(), method),
                new InboundEventContext(event, null));

        Assertions.assertEquals(1, bound.arguments().length);
        Assertions.assertEquals(event.topic(), bound.arguments()[0]);

        final var returned = (Object[]) ((Completed) bound.invoke()).returned();

        Assertions.assertEquals(event.topic(), returned[0]);
    }

    @Test
    public void exception_01() throws Exception {
        final var outcome = binder.bind(
                new InvocableRecord(new InvocableBinderTestCases.ExceptionCase01(),
                        ReflectedClass.reflect(InvocableBinderTestCases.ExceptionCase01.class).findMethod("m01")),
                new InboundEventContext(new InboundEvent(new MockConsumerRecord()), null)).invoke();

        Assertions.assertEquals(IllegalArgumentException.class, ((Failed) outcome).thrown().getClass());
    }

    @Test
    void key_01() {
        final var value = UUID.randomUUID().toString();
        final var case01 = new InvocableBinderTestCases.KeyCase01();

        final var event = MockConsumerRecord.withValue(jackson.toJson(value)).toEvent();
        final var outcome = binder.bind(
                new InvocableRecord(case01,
                        new ReflectedClass<>(InvocableBinderTestCases.KeyCase01.class).findMethod("m01",
                                ConsumerRecord.class, String.class, String.class)),
                new InboundEventContext(event, null)).invoke();

        final var returned = (Object[]) ((Completed) outcome).returned();

        Assertions.assertEquals(true, returned[0] == event.consumerRecord());
        Assertions.assertEquals(true, returned[1].equals(event.key()));
        Assertions.assertEquals(true, returned[2].equals(value));
    }

    @Test
    void partition_01() {
        final var case01 = new InvocableBinderTestCases.ParititionCase01();

        final var event = new MockConsumerRecord().toEvent();
        final var outcome = binder
                .bind(new InvocableRecord(case01, new ReflectedClass<>(InvocableBinderTestCases.ParititionCase01.class)
                        .findMethod("m01", InboundEvent.class, int.class)), new InboundEventContext(event, null))
                .invoke();

        final var returned = (Object[]) ((Completed) outcome).returned();

        Assertions.assertEquals(true, returned[0] == event);
        Assertions.assertEquals(true, returned[1].equals(event.partition()));
    }

    @Test
    void partition_02() {
        final var case01 = new InvocableBinderTestCases.ParititionCase01();

        final var event = new MockConsumerRecord().toEvent();
        final var outcome = binder.bind(new InvocableRecord(case01,
                new ReflectedClass<>(InvocableBinderTestCases.ParititionCase01.class).findMethod("m01", Integer.class)),
                new InboundEventContext(event, null)).invoke();

        final var returned = (Object[]) ((Completed) outcome).returned();

        Assertions.assertEquals(true, returned[0].equals(event.partition()));
    }

    @Test
    void partition_03() {
        final var case01 = new InvocableBinderTestCases.ParititionCase01();

        final var event = new MockConsumerRecord().toEvent();
        final var outcome = binder.bind(new InvocableRecord(case01,
                new ReflectedClass<>(InvocableBinderTestCases.ParititionCase01.class).findMethod("m01", Number.class)),
                new InboundEventContext(event, null)).invoke();

        final var returned = (Object[]) ((Completed) outcome).returned();

        Assertions.assertEquals(true, returned[0].equals(event.partition()));
    }

    @Test
    void offset_01() {
        final var event = new MockConsumerRecord().toEvent();
        final var outcome = binder.bind(new InvocableRecord(new InvocableBinderTestCases.OffsetCase01(),
                new ReflectedClass<>(InvocableBinderTestCases.OffsetCase01.class).findMethod("m01", long.class)),
                new InboundEventContext(event, null)).invoke();

        final var returned = (Object[]) ((Completed) outcome).returned();

        Assertions.assertEquals(event.offset(), returned[0]);
    }

    @Test
    void offset_02() {
        final var event = new MockConsumerRecord().toEvent();
        final var outcome = binder.bind(new InvocableRecord(new InvocableBinderTestCases.OffsetCase01(),
                new ReflectedClass<>(InvocableBinderTestCases.OffsetCase01.class).findMethod("m01", Long.class)),
                new InboundEventContext(event, null)).invoke();

        final var returned = (Object[]) ((Completed) outcome).returned();

        Assertions.assertEquals(event.offset(), returned[0]);
    }

    @Test
    void timestamp_01() {
        final var case01 = new InvocableBinderTestCases.TimestampCase01();
        final var event = new MockConsumerRecord().toEvent();
        final var outcome = binder.bind(new InvocableRecord(case01,
                new ReflectedClass<>(InvocableBinderTestCases.TimestampCase01.class).findMethod("m01", long.class)),
                new InboundEventContext(event, null)).invoke();

        final var returned = (Object[]) ((Completed) outcome).returned();

        Assertions.assertEquals(true, returned[0].equals(event.timestamp()));
    }

    @Test
    void timestamp_02() {
        final var case01 = new InvocableBinderTestCases.TimestampCase01();
        final var event = new MockConsumerRecord().toEvent();
        final var outcome = binder.bind(new InvocableRecord(case01,
                new ReflectedClass<>(InvocableBinderTestCases.TimestampCase01.class).findMethod("m01", Long.class)),
                new InboundEventContext(event, null)).invoke();

        final var returned = (Object[]) ((Completed) outcome).returned();

        Assertions.assertEquals(true, returned[0].equals(event.timestamp()));
    }

    @Test
    void timestamp_03() {
        final var case01 = new InvocableBinderTestCases.TimestampCase01();
        final var event = new MockConsumerRecord().toEvent();
        final var outcome = binder.bind(new InvocableRecord(case01,
                new ReflectedClass<>(InvocableBinderTestCases.TimestampCase01.class).findMethod("m01", Instant.class)),
                new InboundEventContext(event, null)).invoke();

        final var returned = (Object[]) ((Completed) outcome).returned();

        Assertions.assertEquals(true, returned[0].equals(Instant.ofEpochMilli(event.timestamp())));
    }

    @Test
    void value_01() {
        final var bound = binder.bind(
                new InvocableRecord(new InvocableBinderTestCases.ValueCase01(),
                        ReflectedClass.reflect(InvocableBinderTestCases.ValueCase01.class).findMethod("m01",
                                Received.class)),
                new InboundEventContext(new InboundEvent(new MockConsumerRecord()), null));

        Assertions.assertEquals(true, ((Completed) bound.invoke()).returned() == null);
        Assertions.assertEquals(1, bound.arguments().length);
        Assertions.assertEquals(null, bound.arguments()[0]);
    }

    @Test
    void value_02() {
        final var expected = new Account(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        final var bound = binder.bind(
                new InvocableRecord(new InvocableBinderTestCases.ValueCase01(),
                        ReflectedClass.reflect(InvocableBinderTestCases.ValueCase01.class).findMethod("m01",
                                Received.class)),
                MockConsumerRecord.withValue(jackson.toJson(expected)).toEventContext());

        final var returned = (Received) (((Completed) bound.invoke()).returned());

        Assertions.assertEquals(expected.id(), returned.getId());
        Assertions.assertEquals(expected.password(), returned.getPassword());
    }

    @Test
    void value_03() {
        final var expected = new Account(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        final var bound = binder.bind(
                new InvocableRecord(new InvocableBinderTestCases.ValueCase01(),
                        ReflectedClass.reflect(InvocableBinderTestCases.ValueCase01.class).findMethod("m02",
                                Received.class)),
                MockConsumerRecord.withValue(jackson.toJson(expected)).toEventContext());

        final var returned = (Received) (((Completed) bound.invoke()).returned());

        Assertions.assertEquals(expected.id(), returned.getId());
        Assertions.assertEquals(null, returned.getPassword());
    }

    @Test
    void value_04() {
        final var expected = new Account(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        final var bound = binder.bind(
                new InvocableRecord(new InvocableBinderTestCases.ValueCase01(),
                        ReflectedClass.reflect(InvocableBinderTestCases.ValueCase01.class).findMethod("m03",
                                Received.class)),
                MockConsumerRecord.withValue(jackson.toJson(expected)).toEventContext());

        final var returned = (Received) (((Completed) bound.invoke()).returned());

        Assertions.assertEquals(expected.id(), returned.getId());
        Assertions.assertEquals(expected.password(), returned.getPassword());
    }

    @Test
    void header_01() {
        final var bound = binder.bind(
                new InvocableRecord(new InvocableBinderTestCases.HeaderCase01(),
                        ReflectedClass.reflect(InvocableBinderTestCases.HeaderCase01.class).findMethod("m01",
                                String.class)),
                new InboundEventContext(new InboundEvent(new MockConsumerRecord()), null));

        Assertions.assertEquals(true, ((Completed) bound.invoke()).returned() == null);
        Assertions.assertEquals(1, bound.arguments().length);
        Assertions.assertEquals(null, bound.arguments()[0]);
    }

    @Test
    void header_02() {
        final var map = Map.of("prop1", UUID.randomUUID().toString());
        final var event = MockConsumerRecord.withHeaders(StringHeader.headers(map)).toEvent();
        final var bound = binder.bind(new InvocableRecord(new InvocableBinderTestCases.HeaderCase01(),
                new ReflectedClass<>(InvocableBinderTestCases.HeaderCase01.class).findMethod("m01", String.class,
                        String.class)),
                new InboundEventContext(event, null));

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
        final var event = MockConsumerRecord.withHeaders(StringHeader.headers(map)).toEvent();

        final var bound = binder.bind(new InvocableRecord(new InvocableBinderTestCases.HeaderCase01(),
                new ReflectedClass<>(InvocableBinderTestCases.HeaderCase01.class).findMethod("m01", String.class,
                        String.class)),
                new InboundEventContext(event, null));
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
        final var mq = MockConsumerRecord.withHeaders(StringHeader.headers("Prop1", "true")).toEventContext();
        final var bound = binder.bind(new InvocableRecord(new InvocableBinderTestCases.HeaderCase01(),
                new ReflectedClass<>(InvocableBinderTestCases.HeaderCase01.class).findMethod("m01", Boolean.class)),
                mq);
        final var outcome = bound.invoke();

        final var returned = (Boolean) ((Completed) outcome).returned();

        Assertions.assertEquals(true, returned);

        Assertions.assertEquals(1, bound.arguments().length);
        Assertions.assertEquals(true, bound.arguments()[0]);
    }

    @Test
    void header_06() {
        final var event = new InboundEvent(new MockConsumerRecord());
        final var bound = binder.bind(new InvocableRecord(new InvocableBinderTestCases.HeaderCase01(),
                new ReflectedClass<>(InvocableBinderTestCases.HeaderCase01.class).findMethod("m01", Boolean.class)),
                new InboundEventContext(event, null));
        final var outcome = bound.invoke();

        final var returned = (Boolean) ((Completed) outcome).returned();

        Assertions.assertEquals(null, returned);

        Assertions.assertEquals(1, bound.arguments().length);
        Assertions.assertEquals(null, bound.arguments()[0]);
    }

    @Test
    void header_07() {
        final var mq = MockConsumerRecord.withHeaders(StringHeader.headers("prop1", PropertyEnum.Enum1.toString()))
                .toEventContext();
        final var bound = binder.bind(new InvocableRecord(new InvocableBinderTestCases.HeaderCase01(),
                new ReflectedClass<>(InvocableBinderTestCases.HeaderCase01.class).findMethod("m01",
                        PropertyEnum.class)),
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
                .toEventContext();
        final var bound = binder.bind(
                new InvocableRecord(new InvocableBinderTestCases.HeaderCase01(),
                        new ReflectedClass<>(InvocableBinderTestCases.HeaderCase01.class).findMethod("m01", Map.class)),
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
        final var context = MockConsumerRecord.withHeaders(StringHeader.headers("prop1", UUID.randomUUID().toString()))
                .toEventContext();
        final var bound = binder.bind(new InvocableRecord(new InvocableBinderTestCases.HeaderCase01(),
                new ReflectedClass<>(InvocableBinderTestCases.HeaderCase01.class).findMethod("m01", Headers.class)),
                context);
        final var outcome = bound.invoke();
        final var returned = (Object[]) ((Completed) outcome).returned();

        Assertions.assertEquals(1, returned.length);

        Assertions.assertEquals(1, bound.arguments().length);

        Assertions.assertEquals(true, context.event().headers() == (Headers) bound.arguments()[0]);
    }

    @SuppressWarnings("unchecked")
    @Test
    void header_10() {
        final var v1 = UUID.randomUUID().toString();
        final var v2 = UUID.randomUUID().toString();
        final var event = MockConsumerRecord.withHeaders("Iterable", v1, "Iterable", v2).toEventContext();

        final var bound = binder.bind(new InvocableRecord(new InvocableBinderTestCases.HeaderCase01(),
                new ReflectedClass<>(InvocableBinderTestCases.HeaderCase01.class).findMethod("iterableList",
                        Iterable.class, List.class)),
                event);

        final var outcome = bound.invoke();
        final var returned = (Object[]) ((Completed) outcome).returned();

        Assertions.assertEquals(2, returned.length);

        final var iterable = TestUtil.toList(((Iterable<Header>) returned[0]));
        Assertions.assertEquals(2, iterable.size());
        Assertions.assertEquals(v1, TestUtil.valueString(iterable.get(0)));
        Assertions.assertEquals(v2, TestUtil.valueString(iterable.get(1)));
        Assertions.assertEquals(null, returned[1]);
    }

    @SuppressWarnings("unchecked")
    @Test
    void header_11() {
        final var v1 = UUID.randomUUID().toString();
        final var v2 = UUID.randomUUID().toString();
        final var event = MockConsumerRecord.withHeaders("Iterable", v1, "Iterable", v2, "List", v2).toEvent();

        final var bound = binder.bind(new InvocableRecord(new InvocableBinderTestCases.HeaderCase01(),
                new ReflectedClass<>(InvocableBinderTestCases.HeaderCase01.class).findMethod("iterableList",
                        Iterable.class, List.class)),
                new InboundEventContext(event, null));

        final var outcome = bound.invoke();
        final var returned = (Object[]) ((Completed) outcome).returned();

        Assertions.assertEquals(2, returned.length);

        final var iterable = TestUtil.toList(((Iterable<Header>) returned[0]));
        Assertions.assertEquals(2, iterable.size());
        Assertions.assertEquals(v1, TestUtil.valueString(iterable.get(0)));
        Assertions.assertEquals(v2, TestUtil.valueString(iterable.get(1)));

        final var list = (List<String>) returned[1];
        Assertions.assertEquals(v2, list.get(0));
    }

    @Test
    void mdc_01() {
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MdcCase(),
                new ReflectedClass<>(InvocableBinderTestCases.MdcCase.class).findMethod("get"));

        final var bound = binder.bind(invocable,
                new InboundEventContext(new InboundEvent(new MockConsumerRecord()), null));

        Assertions.assertEquals(0, bound.mdcMap().size());
    }

    @Test
    void mdc_02() {
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MdcCase(),
                new ReflectedClass<>(InvocableBinderTestCases.MdcCase.class).findMethod("get", String.class,
                        String.class));

        final var bound = binder.bind(invocable,
                new InboundEventContext(new InboundEvent(new MockConsumerRecord()), null));

        Assertions.assertEquals(1, bound.mdcMap().size());
        Assertions.assertEquals(null, bound.mdcMap().get("name"));
    }

    @Test
    void mdc_03() {
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MdcCase(),
                new ReflectedClass<>(InvocableBinderTestCases.MdcCase.class).findMethod("get", String.class,
                        String.class));
        final var lastName = UUID.randomUUID().toString();
        final var bound = binder.bind(invocable, MockConsumerRecord.withHeaders("LastName", lastName).toEventContext());

        Assertions.assertEquals(1, bound.mdcMap().size());
        Assertions.assertEquals(lastName, bound.mdcMap().get("name"), "should take the last one");
    }

    @Test
    void mdc_04() {
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MdcCase(),
                new ReflectedClass<>(InvocableBinderTestCases.MdcCase.class).findMethod("get", String.class,
                        int.class));
        final var expected = UUID.randomUUID().toString();
        final var bound = binder.bind(invocable, new InboundEventContext(
                new InboundEvent(new MockConsumerRecord(null, jackson.toJson(expected), "Id", "123")), null));

        Assertions.assertEquals(2, bound.mdcMap().size());
        Assertions.assertEquals(expected, bound.mdcMap().get("name"));
        Assertions.assertEquals("123", bound.mdcMap().get("SSN"));
    }

    @Test
    void mdc_05() {
        final var method = new ReflectedClass<>(InvocableBinderTestCases.MdcCase.class).findMethod("get", String.class,
                Integer.class);
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MdcCase(), method);
        final var expected = UUID.randomUUID().toString();
        final var bound = binder.bind(invocable,
                MockConsumerRecord.withValue(jackson.toJson(expected)).toEventContext());

        Assertions.assertEquals(2, bound.mdcMap().size());
        Assertions.assertEquals(expected, bound.mdcMap().get("name"));
        Assertions.assertEquals(null, bound.mdcMap().get("SSN"));
    }

    @Test
    void mdc_06() {
        final var method = new ReflectedClass<>(InvocableBinderTestCases.MdcCase.class).findMethod("getOnBody",
                InvocableBinderTestCases.MdcCase.Name.class);
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MdcCase(), method);
        final var expected = new InvocableBinderTestCases.MdcCase.Name(UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
        final var bound = binder.bind(invocable,
                MockConsumerRecord.withValue(jackson.toJson(expected)).toEventContext());

        Assertions.assertEquals(1, bound.mdcMap().size());
        Assertions.assertEquals(expected.toString(), bound.mdcMap().get("name"), "should take all annotated");
    }

    @Test
    void mdc_06_01() {
        final var method = new ReflectedClass<>(InvocableBinderTestCases.MdcCase.class).findMethod("getOnBody",
                InvocableBinderTestCases.MdcCase.Name.class);
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MdcCase(), method);
        final var bound = binder.bind(invocable, MockConsumerRecord.withValue(jackson.toJson(null)).toEventContext());

        Assertions.assertEquals(1, bound.mdcMap().size());
        Assertions.assertEquals(null, bound.mdcMap().get("name"), "should tolerate null");
    }

    @Test
    void mdc_07() {
        final var method = new ReflectedClass<>(InvocableBinderTestCases.MdcCase.class).findMethod("getInBody",
                InvocableBinderTestCases.MdcCase.Name.class);
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MdcCase(), method);
        final var expected = new InvocableBinderTestCases.MdcCase.Name(UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
        final var bound = binder.bind(invocable,
                MockConsumerRecord.withValue(jackson.toJson(expected)).toEventContext());

        Assertions.assertEquals(0, bound.mdcMap().size(), "should not have any without annotation");
    }

    @Test
    void mdc_08() {
        final var method = new ReflectedClass<>(InvocableBinderTestCases.MdcCase.class).findMethod("getInBody",
                InvocableBinderTestCases.MdcCase.Name.class);
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MdcCase(), method);
        final var expected = new InvocableBinderTestCases.MdcCase.Name(UUID.randomUUID().toString(), null);
        final var bound = binder.bind(invocable,
                MockConsumerRecord.withValue(jackson.toJson(expected)).toEventContext());

        Assertions.assertEquals(0, bound.mdcMap().size());
    }

    @Test
    void mdc_09() {
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MdcCase(),
                new ReflectedClass<>(InvocableBinderTestCases.MdcCase.class).findMethod("getOnBodyIntro",
                        InvocableBinderTestCases.MdcCase.Name.class));

        final var name = new InvocableBinderTestCases.MdcCase.Name(UUID.randomUUID().toString(),
                UUID.randomUUID().toString());

        final var mdcMap = binder.bind(invocable, MockConsumerRecord.withValue(jackson.toJson(name)).toEventContext())
                .mdcMap();

        Assertions.assertEquals(3, mdcMap.size());
        Assertions.assertEquals(name.firstName(), mdcMap.get("firstName"));
        Assertions.assertEquals(name.lastName(), mdcMap.get("lastName"));
        Assertions.assertEquals(name.fullName(), mdcMap.get("fullName"));
    }

    @Test
    void mdc_09_01() {
        final var method = new ReflectedClass<>(InvocableBinderTestCases.MdcCase.class).findMethod("getOnBodyIntro",
                InvocableBinderTestCases.MdcCase.Name.class);
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MdcCase(), method);

        final var mdcMap = binder
                .bind(invocable, new InboundEventContext(new InboundEvent(new MockConsumerRecord()), null)).mdcMap();

        Assertions.assertEquals(3, mdcMap.size());
        Assertions.assertEquals(null, mdcMap.get("firstName"));
        Assertions.assertEquals(null, mdcMap.get("lastName"));
        Assertions.assertEquals(null, mdcMap.get("fullName"));
    }

    @Test
    void mdc_09_02() {
        final var method = new ReflectedClass<>(InvocableBinderTestCases.MdcCase.class).findMethod("getOnBodyIntro",
                InvocableBinderTestCases.MdcCase.Name.class);
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MdcCase(), method);

        final var name = new InvocableBinderTestCases.MdcCase.Name(UUID.randomUUID().toString(), null);

        final var mdcMap = binder.bind(invocable, MockConsumerRecord.withValue(jackson.toJson(name)).toEventContext())
                .mdcMap();

        Assertions.assertEquals(3, mdcMap.size());
        Assertions.assertEquals(name.firstName(), mdcMap.get("firstName"));
        Assertions.assertEquals(name.lastName(), mdcMap.get("lastName"));
        Assertions.assertEquals(name.fullName(), mdcMap.get("fullName"));
    }

    @Test
    void mdc_10() {
        final var method = new ReflectedClass<>(InvocableBinderTestCases.MdcCase.class).findMethod("getOnBodyPrec",
                InvocableBinderTestCases.MdcCase.Name.class, String.class);
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MdcCase(), method);

        final var name = new InvocableBinderTestCases.MdcCase.Name(UUID.randomUUID().toString(),
                UUID.randomUUID().toString());

        final var mdcMap = binder.bind(invocable, MockConsumerRecord.withValue(jackson.toJson(name)).toEventContext())
                .mdcMap();

        Assertions.assertEquals(3, mdcMap.size());
        Assertions.assertEquals(name.firstName(), mdcMap.get("firstName"), "should follow the body");
        Assertions.assertEquals(name.lastName(), mdcMap.get("lastName"));
        Assertions.assertEquals(name.fullName(), mdcMap.get("fullName"));
    }

    @Test
    void mdc_11() {
        final var method = new ReflectedClass<>(InvocableBinderTestCases.MdcCase.class)
                .findMethod("getOnBodyIntroNamed", InvocableBinderTestCases.MdcCase.Name.class);
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MdcCase(), method);

        final var name = new InvocableBinderTestCases.MdcCase.Name(UUID.randomUUID().toString(),
                UUID.randomUUID().toString());

        final var mdcMap = binder.bind(invocable, MockConsumerRecord.withValue(jackson.toJson(name)).toEventContext())
                .mdcMap();

        Assertions.assertEquals(3, mdcMap.size());
        Assertions.assertEquals(name.firstName(), mdcMap.get("Name.firstName"));
        Assertions.assertEquals(name.lastName(), mdcMap.get("Name.lastName"));
        Assertions.assertEquals(name.fullName(), mdcMap.get("Name.fullName"));
    }

    @Test
    void mdc_12() {
        final var method = new ReflectedClass<>(InvocableBinderTestCases.MdcCase.class).findMethod("getOnBodyNamed",
                InvocableBinderTestCases.MdcCase.Name.class, String.class);

        final var name = new InvocableBinderTestCases.MdcCase.Name(UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
        final var firstName = UUID.randomUUID().toString();

        final var invocable = new InvocableRecord(new InvocableBinderTestCases.MdcCase(), method);

        final var mdcMap = binder
                .bind(invocable,
                        new MockConsumerRecord(null, jackson.toJson(name), "FirstName", firstName).toEventContext())
                .mdcMap();

        Assertions.assertEquals(2, mdcMap.size());

        Assertions.assertEquals(name.toString(), mdcMap.get("newName"));
        Assertions.assertEquals(firstName, mdcMap.get("firstName"));
    }
}
