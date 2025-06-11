package me.ehp246.aufkafka.core.reflection;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

public final class ReflectedType {
    private final Type type;

    public ReflectedType(final Type type) {
	this.type = Objects.requireNonNull(type);
    }

    public Type type() {
	return this.type;
    }

    public boolean isParameterizedType() {
	return this.type instanceof ParameterizedType;
    }
}
