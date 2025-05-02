package me.ehp246.aufkafka.core.consumer;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufkafka.api.annotation.ForKey;

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
