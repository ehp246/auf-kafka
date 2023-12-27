package me.ehp246.aufkafka.api.consumer;

import java.util.Set;

/**
 * @author Lei Yang
 * @since 1.0
 */
public interface InvocableScanner {
    Set<InvocableKeyDefinition> apply(final Set<Class<?>> registering,
            final Set<String> scanPackages);
}
