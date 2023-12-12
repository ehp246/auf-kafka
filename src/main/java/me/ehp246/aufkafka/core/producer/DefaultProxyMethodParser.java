package me.ehp246.aufkafka.core.producer;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfHeader;
import me.ehp246.aufkafka.api.annotation.OfKey;
import me.ehp246.aufkafka.api.annotation.OfPartition;
import me.ehp246.aufkafka.api.annotation.OfTimestamp;
import me.ehp246.aufkafka.api.annotation.OfTopic;
import me.ehp246.aufkafka.api.annotation.OfValue;
import me.ehp246.aufkafka.api.producer.OutboundRecord;
import me.ehp246.aufkafka.api.producer.ProxyInvocationBinder.HeaderParam;
import me.ehp246.aufkafka.api.producer.ProxyInvocationBinder.ValueParam;
import me.ehp246.aufkafka.api.producer.ProxyMethodParser;
import me.ehp246.aufkafka.api.serializer.JacksonObjectOf;
import me.ehp246.aufkafka.api.spi.PropertyPlaceholderResolver;
import me.ehp246.aufkafka.core.reflection.ReflectedMethod;
import me.ehp246.aufkafka.core.reflection.ReflectedParameter;
import me.ehp246.aufkafka.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
public final class DefaultProxyMethodParser implements ProxyMethodParser {
    private final PropertyPlaceholderResolver propertyResolver;

    DefaultProxyMethodParser(final PropertyPlaceholderResolver propertyResolver) {
        this.propertyResolver = propertyResolver;
    }

    @Override
    public Parsed parse(final Method method) {
        final var reflected = new ReflectedMethod(method);
        final var byKafka = reflected.method().getDeclaringClass().getAnnotation(ByKafka.class);

        final var topicBinder = reflected.allParametersWith(OfTopic.class).stream().findFirst()
                .map(p -> (Function<Object[], String>) args -> {
                    final Object value = args[p.index()];
                    return value == null ? null : value + "";
                }).orElseGet(() -> {
                    final var topic = propertyResolver.apply(byKafka.value());
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
                .findFirst().map(p -> {
                    final var index = p.index();
                    return (Function<Object[], Object>) args -> args[index];
                }).orElseGet(() -> args -> null);

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
        final var objectOf = Optional
                .ofNullable(valueParamIndex == -1 ? null : reflected.getParameter(valueParamIndex))
                .map(parameter -> new JacksonObjectOf<>(Optional
                        .ofNullable(parameter.getAnnotation(JsonView.class)).map(JsonView::value)
                        .filter(OneUtil::hasValue).map(views -> views[0]).orElse(null),
                        parameter.getType()))
                .orElse(null);

        return new Parsed(new DefaultProxyInvocationBinder(topicBinder, keyBinder, partitionBinder,
                timestampBinder, null,
                valueParamIndex == -1 ? null : new ValueParam(valueParamIndex, objectOf),
                headerBinder(reflected), headerStatic(reflected, byKafka)));
    }

    private Map<Integer, HeaderParam> headerBinder(final ReflectedMethod reflected) {
        final var headerBinder = new HashMap<Integer, HeaderParam>();
        for (final var reflectedParam : reflected.allParametersWith(OfHeader.class)) {
            final var parameter = reflectedParam.parameter();
            headerBinder.put(reflectedParam.index(),
                    new HeaderParam(
                            OneUtil.getIfBlank(parameter.getAnnotation(OfHeader.class).value(),
                                    () -> OneUtil.firstUpper(parameter.getName())),
                            parameter.getType()));
        }
        return headerBinder;
    }

    private List<OutboundRecord.Header> headerStatic(final ReflectedMethod reflected,
            final ByKafka byKafka) {
        final var headers = byKafka.headers();
        if ((headers.length & 1) != 0) {
            throw new IllegalArgumentException("Headers are not in name/value pairs on "
                    + reflected.method().getDeclaringClass());
        }

        final List<OutboundRecord.Header> headerStatic = new ArrayList<>();
        for (int i = 0; i < headers.length; i += 2) {
            final var key = headers[i];
            final var value = propertyResolver.apply(headers[i + 1]);

            headerStatic.add(new OutboundRecord.Header() {

                @Override
                public String key() {
                    return key;
                }

                @Override
                public Object value() {
                    return value;
                }
            });
        }
        return headerStatic;
    }
}
