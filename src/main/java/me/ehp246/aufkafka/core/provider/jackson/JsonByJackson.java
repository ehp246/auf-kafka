package me.ehp246.aufkafka.core.provider.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufkafka.api.serializer.jackson.FromJson;
import me.ehp246.aufkafka.api.serializer.jackson.ToJson;
import me.ehp246.aufkafka.api.serializer.jackson.TypeOfJson;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class JsonByJackson implements FromJson, ToJson {
    private final ObjectMapper objectMapper;

    public JsonByJackson(final ObjectMapper objectMapper) {
        super();
        this.objectMapper = objectMapper;
    }

    @Override
    public String toJson(final Object value, final TypeOfJson typeOf) {
        if (value == null) {
            return null;
        }

        var writer = this.objectMapper.writerFor(this.objectMapper.constructType(typeOf.type()));
        if (typeOf.view() != null) {
            writer = writer.withView(typeOf.view());
        }

        try {
            return writer.writeValueAsString(value);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object fromJson(final String json, final TypeOfJson typeOf) {
        if (json == null || json.isBlank()) {
            return null;
        }

        var reader = this.objectMapper.readerFor(this.objectMapper.constructType(typeOf.type()));
        if (typeOf.view() != null) {
            reader = reader.withView(typeOf.view());
        }

        try {
            return reader.readValue(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
