package me.ehp246.aufkafka.api.consumer;

import java.util.Map;
import java.util.Set;

import me.ehp246.aufkafka.api.annotation.ForEvent;
import me.ehp246.aufkafka.api.annotation.ForKey;

/**
 * The abstraction of the functionality that scans the class path looking for
 * classes that are annotated and considered candidates of
 * {@linkplain EventInvocableDefinition}.
 * 
 * @author Lei Yang
 * @since 1.0
 * @see ForKey
 * @see ForEvent
 * @see EventInvocableNameSource
 */
@FunctionalInterface
public interface InvocableScanner {
    Map<EventInvocableNameSource, Set<EventInvocableDefinition>> apply(final Set<Class<?>> registering,
            final Set<String> scanPackages);
}
