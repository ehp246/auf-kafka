package me.ehp246.aufkafka.api.serializer.json;

import java.util.Objects;

import me.ehp246.aufkafka.api.serializer.JacksonObjectOf;

/**
 * @author Lei Yang
 * @since 1.0
 * @see JacksonObjectOf
 */
public final class JacksonObjectOfBuilder {
    private JacksonObjectOfBuilder() {
        super();
    }

    public static <T> JacksonObjectOf<T> of(final Class<T> type) {
        return new JacksonObjectOf<>(null, type);
    }

    public static <T> JacksonObjectOf<T> ofView(final Class<?> view, final Class<T> type) {
        return new JacksonObjectOf<>(view, type);
    }

    public static <T> JacksonObjectOf<T> ofView(final Class<?> view, final Class<T> type,
            final Class<?>... parameters) {
        Objects.requireNonNull(parameters);

        final var all = new Class<?>[parameters.length + 1];
        all[0] = type;
        System.arraycopy(parameters, 0, all, 1, parameters.length);

        return new JacksonObjectOf<>(view, all);
    }

    /**
     *
     * @param parameters Required. Can not be <code>null</code>
     */
    public static <T> JacksonObjectOf<T> of(final Class<T> type, final Class<?>... parameters) {
        return ofView(null, type, parameters);
    }

}
