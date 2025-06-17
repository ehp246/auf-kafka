package me.ehp246.aufkafka.api.serializer.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Internal abstraction for {@linkplain ObjectMapper} operations.
 *
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface FromJson {
    Object fromJson(String json, TypeOfJson typeOf);
}