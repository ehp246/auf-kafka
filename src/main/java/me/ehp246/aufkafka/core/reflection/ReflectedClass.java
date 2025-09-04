package me.ehp246.aufkafka.core.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public final class ReflectedClass<C> {
    private final Class<C> type;
    private final List<Method> methods;

    public ReflectedClass(final Class<C> type) {
        super();
        this.type = type;
        this.methods = List.of(type.getDeclaredMethods());
    }

    public static <T> ReflectedClass<T> reflect(final Class<T> type) {
        return new ReflectedClass<>(type);
    }

    /**
     * Returns the named method that does not have any parameter. Returns null if
     * not found.
     *
     * @param name
     * @return
     */
    public Method findMethod(final String name) {
        try {
            return type.getMethod(name, (Class<?>[]) null);
        } catch (Exception e) {
            return null;
        }
    }

    public Method findMethod(final String name, final Class<?>... parameters) {
        try {
            return type.getMethod(name, parameters);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns all methods that have the given name ignoring the parameters.
     *
     * @param name
     * @return
     */
    public List<Method> findMethods(final String name) {
        return Stream.of(type.getMethods()).filter(method -> method.getName().equals(name)).toList();
    }

    public Method findSingleNamedMethod(final String name) {
        final var found = this.findMethods(name);
        if (found.size() == 0) {
            return null;
        }

        if (found.size() > 1) {
            throw new IllegalStateException(found.size() + " methods named '" + name + "' found on " + this.type);
        }

        return found.get(0);
    }

    /**
     * Returns all methods that have the given annotation.
     *
     * @param annotationClass
     * @return
     */
    public List<Method> findMethods(final Class<? extends Annotation> annotationClass) {
        return Stream.of(type.getMethods()).filter(method -> method.getDeclaredAnnotation(annotationClass) != null)
                .toList();
    }

    /**
     * Returns the annotated method if there is one and only one, <code>null</code>
     * if there is none. otherwise, it's an exception.
     * 
     * @param annotationClass
     * @return One-and-only annotated method. <code>null</code> if not found
     */
    public Method findSingleAnnotatedMethod(final Class<? extends Annotation> annotationClass) {
        final var found = this.findMethods(annotationClass);
        if (found.size() == 0) {
            return null;
        }

        if (found.size() > 1) {
            throw new IllegalStateException(
                    found.size() + " methods with " + annotationClass.getName() + " found on " + this.type);
        }

        return found.get(0);
    }

    public Class<C> getType() {
        return type;
    }

    public <A extends Annotation> Optional<A> findOnType(final Class<A> annotationType) {
        return Optional.ofNullable(type.getAnnotation(annotationType));
    }

    public Stream<Method> streamSuppliersWith(final Class<? extends Annotation> annotationClass) {
        return this.streamMethodsWith(annotationClass).filter(m -> m.getParameterCount() == 0
                && (m.getReturnType() != void.class && m.getReturnType() != Void.class));
    }

    public Stream<Method> streamMethodsWith(final Class<? extends Annotation> annotationType) {
        return methods.stream().filter(method -> method.isAnnotationPresent(annotationType));
    }
}
