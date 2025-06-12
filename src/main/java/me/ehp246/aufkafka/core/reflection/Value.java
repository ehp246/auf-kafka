package me.ehp246.aufkafka.core.reflection;

import java.lang.reflect.Type;

public record Value(Object value, Type type) {
    public Value(final Object value) {
	this(value, value.getClass());
    }
}
