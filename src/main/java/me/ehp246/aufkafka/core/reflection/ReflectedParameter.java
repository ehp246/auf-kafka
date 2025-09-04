package me.ehp246.aufkafka.core.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author Lei Yang
 *
 */
public final record ReflectedParameter(Parameter parameter, int index) {
    /**
     * Simple type check.
     */
    public boolean isType(final Class<?> type) {
        return this.parameter.getType() == type;
    }

    /**
     * Checks the parameter type is a generic with type argument specified. I.e.,
     * raw generic type on the parameter will return false.
     */
    public boolean isParameterizedType(final Class<?> type) {
        return this.parameter.getType() == type && this.parameter.getParameterizedType() instanceof ParameterizedType;
    }

    public boolean isParameterizedType() {
        return this.parameter.getParameterizedType() instanceof ParameterizedType;
    }

    public boolean isAssignableFrom(final Class<?> type) {
        return this.parameter.getType().isAssignableFrom(type);
    }

    public Type[] getTypeArguments() {
        return ((ParameterizedType) this.parameter.getParameterizedType()).getActualTypeArguments();
    }

    /**
     * Does not do any check.
     */
    public Type getTypeArgument(int index) {
        return ((ParameterizedType) this.parameter.getParameterizedType()).getActualTypeArguments()[index];
    }

    public Class<?> getTypeArgumentAsClass(final int index) {
        return (Class<?>) this.getTypeArgument(index);
    }

    /**
     * Checks the first type argument assuming the parameter is a
     * {@linkplain ParameterizedType}.
     */
    public boolean isTypeArgumentClass(final Class<?> cls) {
        return cls == this.getTypeArgument(0);
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return this.parameter.getAnnotation(annotationClass);
    }

    public Annotation[] getAnnotations() {
        return this.parameter.getAnnotations();
    }

    public String getName() {
        return this.parameter.getName();
    }

    public boolean isEnum() {
        return this.parameter.getType().isEnum();
    }

    public Class<?> getType() {
        return this.parameter.getType();
    }
}
