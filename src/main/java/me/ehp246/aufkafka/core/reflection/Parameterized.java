package me.ehp246.aufkafka.core.reflection;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author Lei Yang
 */
public class Parameterized implements ParameterizedType {
    private final Type ownerType;
    private final Type rawType;
    private final List<Type> typeArgs;

    public Parameterized(Type ownerType, Type rawType, Type... typeArgs) {
        super();
        this.typeArgs = List.of(typeArgs);
        this.rawType = rawType;
        this.ownerType = ownerType;
    }

    public Parameterized(Type rawType, Type... typeArgs) {
        this(null, rawType, typeArgs);
    }

    @Override
    public Type[] getActualTypeArguments() {
        return this.typeArgs.toArray(new Type[] {});
    }

    @Override
    public Type getRawType() {
        return this.rawType;
    }

    @Override
    public Type getOwnerType() {
        return this.ownerType;
    }

}
