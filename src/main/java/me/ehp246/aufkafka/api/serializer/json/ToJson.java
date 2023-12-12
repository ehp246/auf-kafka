package me.ehp246.aufkafka.api.serializer.json;

import me.ehp246.aufkafka.api.serializer.JacksonObjectOf;

/**
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface ToJson {
    /**
     * Should return <code>null</code> for <code>null</code> reference.
     */
    String apply(Object object, JacksonObjectOf<?> objectOf);

    default String apply(final Object object) {
        return this.apply(object, object == null ? null : new JacksonObjectOf<>(object.getClass()));
    }
}
