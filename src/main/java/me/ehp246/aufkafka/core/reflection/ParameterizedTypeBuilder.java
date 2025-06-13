package me.ehp246.aufkafka.core.reflection;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author Lei Yang
 */
public final class ParameterizedTypeBuilder {
    private ParameterizedTypeBuilder() {
    }

    public static ParameterizedType parameterizedType(final Type rawType, final Type... typeArgs) {
        return ParameterizedTypeBuilder.parameterizedType(null, rawType, typeArgs);
    }

    public static ParameterizedType parameterizedType(final Type ownerType, final Type rawType,
            final Type... typeArgs) {
        return new ParameterizedType() {

            @Override
            public Type getRawType() {
                return rawType;
            }

            @Override
            public Type getOwnerType() {
                return ownerType;
            }

            @Override
            public Type[] getActualTypeArguments() {
                return typeArgs;
            }
        };
    }
}
