package me.ehp246.aufkafka.api.serializer;

import java.lang.reflect.Type;

import me.ehp246.aufkafka.core.reflection.ParameterizedTypeBuilder;

/**
 * @author Lei Yang
 */
public interface TypeOfJson {
    Type type();

    default Class<?> view() {
        return null;
    }

    static TypeOfJson newInstanceWithParameterizedType(final Type rawType, final Type typeArg) {
        return newInstance(ParameterizedTypeBuilder.parameterizedType(rawType, new Type[] { typeArg }));
    }

    static TypeOfJson newInstance(final Type type) {
        return TypeOfJson.newInstance(type, null);
    }

    static TypeOfJson newInstance(final Type type, final Class<?> view) {
        return new TypeOfJson() {

            @Override
            public Type type() {
                return type;
            }

            @Override
            public Class<?> view() {
                return view;
            }

        };
    }
}
