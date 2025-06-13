package me.ehp246.aufkafka.api.serializer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Provides typing information to serialize and de-serialize JSON payload.
 * <p>
 * Supports {@linkplain Collection} types, e.g., {@linkplain Set} and
 * {@linkplain List} but not {@linkplain Map}.
 *
 * @author Lei Yang
 * @since 1.0
 */
public final class JacksonObjectOf<T> implements ObjectOfJson<T> {
    public static final JacksonObjectOf<Map<String, Object>> MAP = new JacksonObjectOf<Map<String, Object>>(
            null, Map.class);

    private final List<Class<?>> reifying;

    /**
     * For {@linkplain ObjectWriter#withView(Class)} and
     * {@linkplain ObjectReader#withView(Class)}.
     */
    private final Class<?> view;

    public JacksonObjectOf(final Class<T> type) {
        this(null, type);
    }

    /**
     * At least one type should be specified. Does not accept <code>null</code>'s.
     */
    public JacksonObjectOf(final Class<?> view, final Class<?>... reifying) {
        this.view = view;
        this.reifying = List.of(reifying);
    }

    public Class<?> first() {
        return this.reifying.get(0);
    }

    /**
     * Should have at least one type. Non-modifiable.
     */
    public List<Class<?>> reifying() {
        return this.reifying;
    }

    public Class<?> view() {
        return view;
    }
}
