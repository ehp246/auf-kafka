package me.ehp246.aufkafka.api.serializer.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufkafka.api.serializer.TypeOfJson;

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