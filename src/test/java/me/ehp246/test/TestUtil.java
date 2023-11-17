package me.ehp246.test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import me.ehp246.test.TestUtil.InvocationCaptor;

/**
 * @author Lei Yang
 *
 */
@SuppressWarnings("unchecked")
public class TestUtil {
    public static <T> InvocationCaptor<T> newCaptor(final Class<T> t) {
        final var returnRef = new Object[] { null };
        final var captured = new Invocation[1];
        final var proxy = (T) (Proxy.newProxyInstance(t.getClassLoader(), new Class[] { t },
                (target, method, args) -> {
                    captured[0] = new Invocation(method, target, args);
                    return returnRef[0];
                }));

        return new InvocationCaptor<T>() {

            @Override
            public T proxy() {
                return proxy;
            }

            @Override
            public Invocation invocation() {
                return captured[0];
            }

            @Override
            public InvocationCaptor<T> setReturn(final Object r) {
                returnRef[0] = r;
                return this;
            }
        };
    }

    public interface InvocationCaptor<T> {
        T proxy();

        Invocation invocation();

        InvocationCaptor<T> setReturn(Object ret);
    }

    public record Invocation(Method method, Object target, Object[] args) {
    }
}
