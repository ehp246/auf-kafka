package me.ehp246.aufkafka.core.producer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfCorrelationId;
import me.ehp246.aufkafka.api.annotation.OfHeader;
import me.ehp246.aufkafka.api.annotation.OfKey;
import me.ehp246.aufkafka.api.annotation.OfPartition;
import me.ehp246.aufkafka.api.annotation.OfTimestamp;
import me.ehp246.aufkafka.api.annotation.OfTopic;
import me.ehp246.aufkafka.api.producer.ProxyMethodParser;
import me.ehp246.aufkafka.api.spi.PropertyResolver;
import me.ehp246.aufkafka.core.reflection.ReflectedMethod;
import me.ehp246.aufkafka.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
public final class DefaultProxyMethodParser implements ProxyMethodParser {
    private final static Set<Class<? extends Annotation>> PARAMETER_ANNOTATIONS = Set.of(
            OfTopic.class, OfKey.class, OfPartition.class, OfHeader.class, OfCorrelationId.class);

    private final PropertyResolver propertyResolver;

    DefaultProxyMethodParser(final PropertyResolver propertyResolver) {
        this.propertyResolver = propertyResolver;
    }

    @Override
    public Parsed parse(final Method method) {
        final var reflected = new ReflectedMethod(method);
        final var byKafka = reflected.findOnMethodUp(ByKafka.class).get();

        final var topicBinder = reflected.allParametersWith(OfTopic.class).stream().findFirst()
                .map(p -> (Function<Object[], String>) args -> {
                    Object value = args[p.index()];
                    final Object value1 = value;
                    return value1 == null ? null : value1 + "";
                }).orElseGet(() -> {
                    final var topic = propertyResolver.resolve(byKafka.value());
                    return (Function<Object[], String>) args -> topic;
                });

        final var keyBinder = reflected.allParametersWith(OfKey.class).stream().findFirst()
                .map(p -> (Function<Object[], String>) args -> {
                    final var value = args[p.index()];
                    return value == null ? null : value + "";
                }).orElseGet(() -> {
                    String key = OneUtil.firstUpper(reflected.method().getName());
                    return args -> key;
                });

        final var partitionBinder = reflected.allParametersWith(OfPartition.class).stream().findFirst()
                .map(p -> (Function<Object[], Integer>) args -> (Integer) args[p.index()]).orElseGet(() -> args -> null);

        final var timestampBinder = reflected.allParametersWith(OfTimestamp.class).stream().findFirst()
                .map(p -> (Function<Object[], Instant>) args -> (Instant) args[p.index()]).orElseGet(() -> args -> null);

        return new Parsed(
                new DefaultProxyInvocationBinder(topicBinder, keyBinder, partitionBinder, timestampBinder, null));
    }
}
