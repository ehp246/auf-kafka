package me.ehp246.aufkafka.core.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @author Lei Yang
 *
 */
public final class ReflectedMethod {
    private final Class<?> declaringType;
    private final Method method;
    private final Parameter[] parameters;
    private final List<Class<?>> exceptionTypes;

    public ReflectedMethod(final Method method) {
        this.method = Objects.requireNonNull(method);
        this.declaringType = method.getDeclaringClass();
        this.parameters = method.getParameters();
        this.exceptionTypes = List.of(method.getExceptionTypes());
    }

    public Optional<ReflectedParameter> firstPayloadParameter(final Set<Class<? extends Annotation>> exclusions) {
        for (var i = 0; i < parameters.length; i++) {
            final var parameter = parameters[i];
            if (exclusions.stream().filter(type -> parameter.isAnnotationPresent(type)).findAny().isEmpty()) {
                return Optional.of(new ReflectedParameter(parameter, i));
            }
        }

        return Optional.empty();
    }

    public List<ReflectedParameter> allParametersWith(final Class<? extends Annotation> annotationType) {
        final var list = new ArrayList<ReflectedParameter>();

        for (int i = 0; i < parameters.length; i++) {
            final var parameter = parameters[i];
            if (parameter.isAnnotationPresent(annotationType)) {
                list.add(new ReflectedParameter(parameter, i));
            }
        }

        return list;
    }

    public Method method() {
        return this.method;
    }

    public Parameter getParameter(final int index) {
        return this.parameters[index];
    }

    public <A extends Annotation> Optional<A> findOnMethodUp(final Class<A> annotationClass) {
        final var found = method.getAnnotation(annotationClass);
        if (found != null) {
            return Optional.of(found);
        }

        return Optional.ofNullable(declaringType.getAnnotation(annotationClass));
    }

    public <A extends Annotation> Optional<A> findOnMethod(final Class<A> annotationClass) {
        final var found = method.getAnnotation(annotationClass);
        return Optional.ofNullable(found);
    }

    /**
     * Is the given type on the <code>throws</code>. Must be explicitly declared.
     * Not on the clause doesn't mean the exception can not be thrown by the method,
     * e.g., all runtime exceptions.
     */
    public boolean isOnThrows(final Class<?> type) {
        return RuntimeException.class.isAssignableFrom(type)
                || this.exceptionTypes.stream().filter(t -> t.isAssignableFrom(type)).findAny().isPresent();
    }

    public boolean returnsVoid() {
        final var returnType = method.getReturnType();

        return returnType == void.class || returnType == Void.class;
    }

    public boolean returnsType(Class<?> type) {
        return method.getReturnType() == type;
    }

    /**
     * Assuming the return type is generic.
     * 
     * @return {@linkplain ParameterizedType} of the return type.
     */
    public ParameterizedType parameterizedReturnType() {
        return (ParameterizedType) method.getGenericReturnType();
    }

    public boolean isReturnTypeParameterized() {
        return method.getGenericReturnType() instanceof ParameterizedType;
    }

    public boolean isReturnTypeParameterized(Class<?> type) {
        return this.isReturnTypeParameterized() && this.parameterizedReturnType().getRawType() == type;
    }

    public Type[] returnTypeTypeArguments() {
        return this.parameterizedReturnType().getActualTypeArguments();
    }

    public boolean returnTypeDeclaresTypeArguments(Class<?>... typeArgs) {
        return Arrays.equals(typeArgs, this.returnTypeTypeArguments());
    }

    public boolean isReturnTypeParameterizedWithTypeArguments(final Class<?> type, final Class<?>... typeArgs) {
        return this.isReturnTypeParameterized(type) && this.returnTypeDeclaresTypeArguments(typeArgs);
    }
}
