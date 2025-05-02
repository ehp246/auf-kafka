package me.ehp246.aufkafka.api.consumer;

import java.util.Map;
import java.util.Set;

import me.ehp246.aufkafka.api.annotation.ForEventType;
import me.ehp246.aufkafka.api.annotation.ForKey;

/**
 * The abstraction of the functionality that scans the class path looking for
 * classes that are considered candidates of
 * {@linkplain EventInvocableDefinition}.
 * 
 * @author Lei Yang
 * @since 1.0
 * @see ForKey
 * @see ForEventType
 * @see EventInvocableKeyType
 */
@FunctionalInterface
public interface InvocableScanner {
    Map<EventInvocableKeyType, Set<EventInvocableDefinition>> apply(final Set<Class<?>> registering,
            final Set<String> scanPackages);
}
