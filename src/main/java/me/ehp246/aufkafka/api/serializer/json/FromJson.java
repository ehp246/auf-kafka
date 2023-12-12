package me.ehp246.aufkafka.api.serializer.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufkafka.api.serializer.JacksonObjectOf;

/**
 * Internal abstraction for {@linkplain ObjectMapper} operations.
 *
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface FromJson {
    /**
     *
     * @param json     serialized-JSON string
     * @param objectOf Could be <code>null</code>. In which case, it is up to
     *                 {@linkplain ObjectMapper}.
     * @return de-serialized Java object
     */
    <T> T apply(final String json, final JacksonObjectOf<T> objectOf);
}