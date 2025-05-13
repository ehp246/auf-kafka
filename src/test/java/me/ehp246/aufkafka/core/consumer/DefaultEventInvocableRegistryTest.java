package me.ehp246.aufkafka.core.consumer;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufkafka.api.consumer.EventInvocableDefinition;
import me.ehp246.aufkafka.api.consumer.EventInvocableLookupType;
import me.ehp246.test.mock.MockConsumerRecord;

class DefaultEventInvocableRegistryTest {

    @Test
    void key_01() {
        final var method = String.class.getMethods()[0];
        final var event = MockConsumerRecord.withKey();
        final var registry = new DefaultEventInvocableRegistry("");

        registry.register(EventInvocableLookupType.KEY,
                new EventInvocableDefinition(Set.of(event.key()), String.class, Map.of("", method)));

        final var invocable = registry.resolve(event);

        Assertions.assertEquals(method, invocable.method());
        ;
    }

    @Test
    void key_02() {
        final var method = String.class.getMethods()[0];
        final var eventTypeHeader = UUID.randomUUID().toString();
        final var event = MockConsumerRecord.withKey();

        final var registry = new DefaultEventInvocableRegistry(eventTypeHeader);

        registry.register(EventInvocableLookupType.EVENT_HEADER,
                new EventInvocableDefinition(Set.of(event.key()), Map.class, Map.of("", Map.class.getMethods()[0])));

        registry.register(EventInvocableLookupType.KEY,
                new EventInvocableDefinition(Set.of(event.key()), String.class, Map.of("", method)));

        final var invocable = registry.resolve(event);

        Assertions.assertEquals(method, invocable.method(), "should use the key without the header");
    }

    @Test
    void header_01() {
        final var method = String.class.getMethods()[0];
        final var eventTypeHeader = UUID.randomUUID().toString();
        final var eventType = UUID.randomUUID().toString();
        final var event = MockConsumerRecord.withHeaders(eventTypeHeader, eventType);
        final var registry = new DefaultEventInvocableRegistry(eventTypeHeader);

        registry.register(EventInvocableLookupType.EVENT_HEADER,
                new EventInvocableDefinition(Set.of(eventType), String.class, Map.of("", method)));

        final var invocable = registry.resolve(event);

        Assertions.assertEquals(method, invocable.method());
    }

    @Test
    void header_02() {
        final var method = String.class.getMethods()[0];
        final var eventTypeHeader = UUID.randomUUID().toString();
        final var eventType = UUID.randomUUID().toString();
        final var event = MockConsumerRecord.withKeyAndHeaders(eventType, eventTypeHeader, eventType);

        final var registry = new DefaultEventInvocableRegistry(eventTypeHeader);

        registry.register(EventInvocableLookupType.EVENT_HEADER,
                new EventInvocableDefinition(Set.of(eventType), String.class, Map.of("", method)));

        registry.register(EventInvocableLookupType.KEY,
                new EventInvocableDefinition(Set.of(eventType), Map.class, Map.of("", Map.class.getMethods()[0])));

        final var invocable = registry.resolve(event);

        Assertions.assertEquals(method, invocable.method(), "should use the header instead of key");
    }

    @Test
    void mixed_01() {
        final var headerMethod = String.class.getMethods()[0];
        final var eventTypeHeader = UUID.randomUUID().toString();
        final var eventType = UUID.randomUUID().toString();

        final var registry = new DefaultEventInvocableRegistry(eventTypeHeader);

        registry.register(EventInvocableLookupType.EVENT_HEADER,
                new EventInvocableDefinition(Set.of(eventType), String.class, Map.of("", headerMethod)));

        final var keyMethod = Map.class.getMethods()[0];
        registry.register(EventInvocableLookupType.KEY,
                new EventInvocableDefinition(Set.of(eventType), Map.class, Map.of("", keyMethod)));

        Assertions.assertEquals(headerMethod,
                registry.resolve(MockConsumerRecord.withKeyAndHeaders(eventType, eventTypeHeader, eventType)).method(),
                "should use the header instead of key");

        Assertions.assertEquals(keyMethod, registry.resolve(MockConsumerRecord.withKey(eventType)).method(),
                "should use the key instead of header");

        Assertions.assertEquals(headerMethod,
                registry.resolve(MockConsumerRecord.withKeyAndHeaders(eventType, eventTypeHeader, eventType)).method(),
                "should use the header instead of key");

        Assertions.assertEquals(keyMethod, registry.resolve(MockConsumerRecord.withKey(eventType)).method(),
                "should use the key instead of header");
    }

    @Test
    void error_01() {
        final var registry = new DefaultEventInvocableRegistry("");
        final var keyValue = UUID.randomUUID().toString();

        registry.register(EventInvocableLookupType.KEY,
                new EventInvocableDefinition(Set.of(keyValue), Map.class, Map.of("", Map.class.getMethods()[0])));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> registry.register(EventInvocableLookupType.KEY, new EventInvocableDefinition(Set.of(keyValue),
                        String.class, Map.of("", String.class.getMethods()[0]))));

        Assertions
                .assertDoesNotThrow(
                        () -> registry.register(EventInvocableLookupType.EVENT_HEADER,
                                new EventInvocableDefinition(Set.of(keyValue), Map.class,
                                        Map.of("", Map.class.getMethods()[0]))),
                        "should be okay for different key type");
    }

    @Test
    void error_02() {
        final var registry = new DefaultEventInvocableRegistry("");
        Assertions.assertEquals(null,
                registry.resolve(MockConsumerRecord.withKeyAndHeaders(UUID.randomUUID().toString(),
                        UUID.randomUUID().toString(), UUID.randomUUID().toString())));
    }

    @Test
    void registered_01() {
        final var registry = new DefaultEventInvocableRegistry("");

        Assertions.assertEquals(true, registry.registered(EventInvocableLookupType.KEY).isEmpty());
    }
}
