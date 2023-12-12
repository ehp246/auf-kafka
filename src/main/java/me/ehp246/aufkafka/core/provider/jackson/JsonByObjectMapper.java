package me.ehp246.aufkafka.core.provider.jackson;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufkafka.api.exception.ObjectMapperException;
import me.ehp246.aufkafka.api.serializer.JacksonObjectOf;
import me.ehp246.aufkafka.api.serializer.json.FromJson;
import me.ehp246.aufkafka.api.serializer.json.ToJson;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class JsonByObjectMapper implements FromJson, ToJson {
    private final ObjectMapper objectMapper;

    public JsonByObjectMapper(final ObjectMapper objectMapper) {
        super();
        this.objectMapper = objectMapper;
    }

    @Override
    public String apply(final Object value, final JacksonObjectOf<?> valueInfo) {
        if (value == null) {
            return null;
        }

        final var type = valueInfo == null ? value.getClass() : valueInfo.first();
        final var view = valueInfo == null ? null : valueInfo.view();

        try {
            if (view == null) {
                return this.objectMapper.writerFor(type).writeValueAsString(value);
            } else {
                return this.objectMapper.writerFor(type).withView(view).writeValueAsString(value);
            }
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T apply(final String json, JacksonObjectOf<T> descriptor) {
        descriptor = descriptor == null ? (JacksonObjectOf<T>) new JacksonObjectOf<>(Object.class) : descriptor;

        if (json == null || json.isBlank()) {
            return null;
        }

        final var type = Objects.requireNonNull(descriptor.reifying().get(0));
        final var reifying = descriptor.reifying();

        final var reader = Optional.ofNullable(descriptor.view())
                .map(view -> objectMapper.readerWithView(view)).orElseGet(objectMapper::reader);
        try {
            if (reifying.size() == 1) {
                return reader.forType(type).readValue(json);
            } else {
                final var typeFactory = objectMapper.getTypeFactory();
                final var types = new ArrayList<Class<?>>(reifying);

                final var size = types.size();
                var javaType = typeFactory.constructParametricType(types.get(size - 2),
                        types.get(size - 1));
                for (int i = size - 3; i >= 0; i--) {
                    javaType = typeFactory.constructParametricType(types.get(i), javaType);
                }

                return reader.forType(javaType).readValue(json);
            }
        } catch (final JsonProcessingException e) {
            throw new ObjectMapperException(e);
        }
    }
}
