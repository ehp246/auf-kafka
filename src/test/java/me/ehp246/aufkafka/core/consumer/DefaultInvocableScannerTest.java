package me.ehp246.aufkafka.core.consumer;

import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufkafka.api.annotation.ForKey;
import me.ehp246.aufkafka.api.consumer.EventInvocableDefinition;
import me.ehp246.aufkafka.api.consumer.EventInvocableKeyType;
import me.ehp246.aufkafka.api.consumer.InstanceScope;
import me.ehp246.aufkafka.api.consumer.InvocationModel;
import me.ehp246.aufkafka.core.consumer.case02.TestCase02;

class DefaultInvocableScannerTest {

    @Test
    void test_01() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new DefaultInvocableScanner(Object::toString).apply(Set.of(NonPublic.class), null));
    }

    @Test
    void test_02() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DefaultInvocableScanner(Object::toString)
                .apply(null, Set.of("me.ehp246.aufkafka.core.consumer.case01")));
    }

    @Test
    void test_03() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new DefaultInvocableScanner(Object::toString).apply(Set.of(NoAnnotation.class), null),
                "should require annotations to register");
    }

    @Test
    void test_04() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DefaultInvocableScanner(Object::toString)
                .apply(Set.of(me.ehp246.aufkafka.core.consumer.case03.TestCase03.NotMsg.class), null));
    }

    @Test
    void test_05() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DefaultInvocableScanner(Object::toString)
                .apply(Set.of(me.ehp246.aufkafka.core.consumer.case03.TestCase03.NoEnum.class), null));
    }

    @Test
    void test_06() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DefaultInvocableScanner(Object::toString)
                .apply(Set.of(me.ehp246.aufkafka.core.consumer.case03.TestCase03.NoDup.class), null));
    }

    @Test
    void test_07() {
        Assertions.assertThrows(NoSuchElementException.class, () -> new DefaultInvocableScanner(Object::toString)
                .apply(Set.of(me.ehp246.aufkafka.core.consumer.case03.TestCase03.NoApply.class), null));
    }

    @Test
    void test_08() {
        Assertions.assertThrows(IllegalStateException.class, () -> new DefaultInvocableScanner(Object::toString)
                .apply(Set.of(me.ehp246.aufkafka.core.consumer.case03.TestCase03.ManyApply.class), null));
    }

    @Test
    void test_09() {
        Assertions.assertThrows(IllegalStateException.class, () -> new DefaultInvocableScanner(Object::toString)
                .apply(Set.of(me.ehp246.aufkafka.core.consumer.case03.TestCase03.ManyApplying.class), null));
    }

    @Test
    void key_type_register_01() {
        final var mapped = new DefaultInvocableScanner(Object::toString).apply(Set.of(TestCase02.ForKey01.class), null);

        Assertions.assertEquals(1, mapped.keySet().size());
        Assertions.assertEquals(true, mapped.keySet().contains(EventInvocableKeyType.KEY));
    }

    @Test
    void key_type_register_02() {
        final var mapped = new DefaultInvocableScanner(Object::toString).apply(Set.of(TestCase02.ForEventType01.class),
                null);

        Assertions.assertEquals(1, mapped.keySet().size());
        Assertions.assertEquals(true, mapped.keySet().contains(EventInvocableKeyType.EVENT_HEADER));
    }

    @Test
    void key_type_register_03() {
        final var mapped = new DefaultInvocableScanner(Object::toString).apply(Set.of(TestCase02.ForCombined01.class),
                null);

        final var forKeySet = mapped.get(EventInvocableKeyType.KEY);

        Assertions.assertEquals(1, forKeySet.size());

        Assertions.assertThrows(UnsupportedOperationException.class, forKeySet::clear);

        final var forKeyDefs = forKeySet.toArray(new EventInvocableDefinition[0]);

        Assertions.assertEquals(1, forKeyDefs.length);
        Assertions.assertEquals(true, forKeyDefs[0].lookupKeys().contains("key-test"));
        Assertions.assertEquals(true, forKeyDefs[0].model() == InvocationModel.INLINE);
        Assertions.assertEquals(true, forKeyDefs[0].scope() == InstanceScope.BEAN);

        final var eventTypeSet = mapped.get(EventInvocableKeyType.EVENT_HEADER);

        Assertions.assertEquals(1, eventTypeSet.size());

        final var eventTypeDefs = eventTypeSet.toArray(new EventInvocableDefinition[0]);

        Assertions.assertEquals(1, eventTypeDefs.length);
        Assertions.assertEquals(true, eventTypeDefs[0].lookupKeys().contains("event-type-test"));
        Assertions.assertEquals(true, eventTypeDefs[0].model() == InvocationModel.DEFAULT);
        Assertions.assertEquals(true, eventTypeDefs[0].scope() == InstanceScope.MESSAGE);
    }

    @Test
    void key_type_scan_01() {
        final var mapped = new DefaultInvocableScanner(Object::toString).apply(null,
                Set.of("me.ehp246.aufkafka.core.consumer.case02"));

        final var forKeySet = mapped.get(EventInvocableKeyType.KEY);

        Assertions.assertEquals(2, forKeySet.size(), "should accept duplicated lookup keys");

        Assertions.assertThrows(UnsupportedOperationException.class, forKeySet::clear);

        final var forKeyDefs = forKeySet.toArray(new EventInvocableDefinition[0]);

        Assertions.assertEquals(2, forKeyDefs.length);
        Assertions.assertEquals(true, forKeyDefs[0].lookupKeys().contains("key-test"));
        Assertions.assertEquals(true, forKeyDefs[1].lookupKeys().contains("key-test"));

        final var eventTypeSet = mapped.get(EventInvocableKeyType.EVENT_HEADER);

        Assertions.assertEquals(2, eventTypeSet.size());

        final var eventTypeDefs = eventTypeSet.toArray(new EventInvocableDefinition[0]);

        Assertions.assertEquals(2, eventTypeDefs.length);
        Assertions.assertEquals(true, eventTypeDefs[0].lookupKeys().contains("event-type-test"));
        Assertions.assertEquals(true, eventTypeDefs[1].lookupKeys().contains("event-type-test"));
    }

    @ForKey("test")
    static class NonPublic {
        public void invoke() {
        }
    }

    public static class NoAnnotation {
        public void invoke() {
        }
    }
}
