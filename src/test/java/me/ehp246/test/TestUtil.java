package me.ehp246.test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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

        return new InvocationCaptor<T>(proxy);
    }

    public static class InvocationCaptor<T> {
        private final T proxy;
        private final Invocation[] invocationRef = new Invocation[1];
        private final Object[] returnRef = new Object[] { null };

        InvocationCaptor(final T proxy) {
            super();
            this.proxy = proxy;
        }

        void setInvocation(final Invocation invocation) {
            this.invocationRef[0] = invocation;
        }

        public T proxy() {
            return proxy;
        }

        public Invocation invocation() {
            return invocationRef[0];
        }
    }

    public record Invocation(Method method, Object target, Object[] args) {
    }
}
