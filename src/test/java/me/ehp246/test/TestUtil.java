package me.ehp246.test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import me.ehp246.aufkafka.api.producer.OutboundRecord.Header;
import me.ehp246.aufkafka.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
@SuppressWarnings("unchecked")
public final class TestUtil {
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).registerModule(new JavaTimeModule())
            .registerModule(new MrBeanModule()).registerModule(new ParameterNamesModule());

    public static <T> InvocationCaptor<T> newCaptor(final Class<T> t) {
        final var returnRef = new Object[] { null };
        final var captured = new Invocation[1];
        final var proxy = (T) (Proxy.newProxyInstance(t.getClassLoader(), new Class[] { t }, (target, method, args) -> {
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

    public static String getLastValue(final Iterable<Header> headers, final String key) {
        if (headers == null) {
            return null;
        }

        return OneUtil.toString(StreamSupport.stream(headers.spliterator(), false)
                .filter(header -> header.key().equals(key)).collect(Collectors.toMap(header -> header.key(), header -> {
                    final var list = new ArrayList<>();
                    list.add(header.value());
                    return list;
                }, (l, r) -> {
                    l.addAll(r);
                    return l;
                })).get(key).getLast());
    }
}
