package me.ehp246.aufkafka.api.serializer.jackson;

/**
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface ToJson {
    /**
     * Should return <code>null</code> for <code>null</code> reference.
     */
    String toJson(Object value, TypeOfJson typeOf);

    default String toJson(final Object value) {
        return this.toJson(value, value == null ? null : TypeOfJson.of(value.getClass()));
    }
}
