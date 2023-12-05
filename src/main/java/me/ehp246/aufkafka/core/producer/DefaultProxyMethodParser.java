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
import me.ehp246.aufkafka.api.annotation.OfValue;
import me.ehp246.aufkafka.api.producer.ProxyMethodParser;
import me.ehp246.aufkafka.api.spi.PropertyResolver;
import me.ehp246.aufkafka.core.reflection.ReflectedMethod;
import me.ehp246.aufkafka.core.reflection.ReflectedParameter;
import me.ehp246.aufkafka.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
public final class DefaultProxyMethodParser implements ProxyMethodParser {
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
                }).orElseGet(() -> reflected.findOnMethodUp(OfKey.class).map(ofKey -> {
                    final var key = ofKey.value();
                    return key.isBlank() ? (Function<Object[], String>) args -> null
                            : (Function<Object[], String>) args -> key;
                }).orElseGet(() -> {
                    String key = OneUtil.firstUpper(reflected.method().getName());
                    return args -> key;
                }));

        final var partitionBinder = reflected.allParametersWith(OfPartition.class).stream()
                .findFirst()
                .map(p -> {
                    final var index = p.index();
                    return (Function<Object[], Object>) args -> args[index];
                })
                .orElseGet(() -> args -> null);

        final var timestampBinder = reflected.allParametersWith(OfTimestamp.class).stream()
                .findFirst().map(p -> {
                    final var index = p.index();
                    final var type = p.parameter().getType();
                    if (type == Instant.class) {
                        return (Function<Object[], Instant>) args -> (Instant) args[index];
                    }
                    if (type == Long.class) {
                        return (Function<Object[], Instant>) args -> {
                            final var value = args[index];
                            return value == null ? null : Instant.ofEpochMilli((Long) value);
                        };
                    }
                    if (type == long.class) {
                        return (Function<Object[], Instant>) args -> Instant
                                .ofEpochMilli((long) args[index]);
                    }
                    throw new IllegalArgumentException(
                            "Un-supported type " + type + " on " + p.parameter());
                }).orElseGet(() -> args -> null);

        final var valueParamIndex = reflected.allParametersWith(OfValue.class).stream().findFirst()
                .map(ReflectedParameter::index).orElse(-1);

        return new Parsed(new DefaultProxyInvocationBinder(topicBinder, keyBinder, partitionBinder,
                timestampBinder, null, valueParamIndex));
    }
}
