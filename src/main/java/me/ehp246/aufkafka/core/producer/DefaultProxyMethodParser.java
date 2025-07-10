package me.ehp246.aufkafka.core.producer;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfHeader;
import me.ehp246.aufkafka.api.annotation.OfKey;
import me.ehp246.aufkafka.api.annotation.OfPartition;
import me.ehp246.aufkafka.api.annotation.OfTimestamp;
import me.ehp246.aufkafka.api.annotation.OfTopic;
import me.ehp246.aufkafka.api.annotation.OfValue;
import me.ehp246.aufkafka.api.exception.ProxyReturnBindingException;
import me.ehp246.aufkafka.api.producer.OutboundEvent;
import me.ehp246.aufkafka.api.producer.ProducerFn.ProducerFnRecord;
import me.ehp246.aufkafka.api.serializer.jackson.TypeOfJson;
import me.ehp246.aufkafka.api.spi.ExpressionResolver;
import me.ehp246.aufkafka.core.producer.ProxyInvocationBinder.HeaderParam;
import me.ehp246.aufkafka.core.producer.ProxyInvocationBinder.ValueParam;
import me.ehp246.aufkafka.core.reflection.ReflectedMethod;
import me.ehp246.aufkafka.core.reflection.ReflectedParameter;
import me.ehp246.aufkafka.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
public final class DefaultProxyMethodParser implements ProxyMethodParser {
    private final ExpressionResolver expressionResolver;

    DefaultProxyMethodParser(final ExpressionResolver expressionResolver) {
        this.expressionResolver = expressionResolver;
    }

    @Override
    public Parsed parse(final Method method) {
        final var reflected = new ReflectedMethod(method);
        return new Parsed(parseInvocationBinder(reflected), parseReturnBinder(reflected));
    }

    private ProxyInvocationBinder parseInvocationBinder(final ReflectedMethod reflected) {
        final var byKafka = reflected.method().getDeclaringClass().getAnnotation(ByKafka.class);

        final var topicBinder = reflected.allParametersWith(OfTopic.class).stream().findFirst()
                .map(p -> (Function<Object[], String>) args -> {
                    final Object value = args[p.index()];
                    return value == null ? null : value + "";
                }).orElseGet(() -> {
                    final var topic = expressionResolver.apply(byKafka.value());
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
                }).orElseGet(() -> args -> null));

        final var partitionBinder = reflected.allParametersWith(OfPartition.class).stream().findFirst().map(p -> {
            final var index = p.index();
            final var type = p.parameter().getType();
            if (!Integer.class.isAssignableFrom(type) && !int.class.isAssignableFrom(type)) {
                throw new UnsupportedOperationException(
                        "Un-supported type on parameter " + index + " of " + reflected.method());
            }
            return (Function<Object[], Integer>) args -> (Integer) args[index];

        }).orElseGet(() -> args -> null);

        final var timestampBinder = reflected.allParametersWith(OfTimestamp.class).stream().findFirst().map(p -> {
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
                return (Function<Object[], Instant>) args -> Instant.ofEpochMilli((long) args[index]);
            }
            throw new IllegalArgumentException("Un-supported type " + type + " on " + p.parameter());
        }).orElseGet(() -> args -> null);

        final var valueParamIndex = reflected.allParametersWith(OfValue.class).stream().findFirst()
                .map(ReflectedParameter::index).orElse(-1);
        final var typeOf = Optional.ofNullable(valueParamIndex == -1 ? null : reflected.getParameter(valueParamIndex))
                .map(parameter -> TypeOfJson.of(parameter.getParameterizedType(),
                        Optional.ofNullable(parameter.getAnnotation(JsonView.class)).map(JsonView::value)
                                .filter(OneUtil::hasValue).map(views -> views[0]).orElse(null)))
                .orElse(null);

        return new DefaultProxyInvocationBinder(topicBinder, keyBinder, partitionBinder, timestampBinder,
                valueParamIndex == -1 ? null : new ValueParam(valueParamIndex, typeOf), headerBinder(reflected),
                headerStatic(reflected, byKafka));
    }

    private Map<Integer, HeaderParam> headerBinder(final ReflectedMethod reflected) {
        final var headerBinder = new HashMap<Integer, HeaderParam>();
        for (final var reflectedParam : reflected.allParametersWith(OfHeader.class)) {
            final var parameter = reflectedParam.parameter();
            headerBinder.put(reflectedParam.index(),
                    new HeaderParam(OneUtil.getIfBlank(parameter.getAnnotation(OfHeader.class).value(),
                            () -> OneUtil.firstUpper(parameter.getName())), parameter.getType()));
        }
        return headerBinder;
    }

    private List<OutboundEvent.Header> headerStatic(final ReflectedMethod reflected, final ByKafka byKafka) {
        final var headers = byKafka.headers();
        if ((headers.length & 1) != 0) {
            throw new IllegalArgumentException(
                    "Headers are not in name/value pairs on " + reflected.method().getDeclaringClass());
        }

        final List<OutboundEvent.Header> headerStatic = new ArrayList<>();

        if (!byKafka.methodAsEvent().isEmpty()) {
            headerStatic
                    .add(new OutboundHeader(byKafka.methodAsEvent(), OneUtil.firstUpper(reflected.method().getName())));
        }

        for (int i = 0; i < headers.length; i += 2) {
            final var key = headers[i];
            final var value = expressionResolver.apply(headers[i + 1]);

            headerStatic.add(new OutboundHeader(key, value));
        }

        return headerStatic;
    }

    private ProxyReturnBinder parseReturnBinder(final ReflectedMethod reflected) {
        if (reflected.returnsVoid()) {
            return (LocalReturnBinder) (event, sent) -> null;
        }
        /*
         * Simple types first
         */
        final var type = reflected.method().getGenericReturnType();
        if (type == RecordMetadata.class) {
            return (LocalReturnBinder) (event, sent) -> wrapGet(sent.future());
        } else if (type == ProducerFnRecord.class) {
            return (LocalReturnBinder) (event, sent) -> sent;
        } else if (type == OutboundEvent.class) {
            return (LocalReturnBinder) (event, sent) -> event;
        } else if (reflected.isReturnTypeParameterizedWithTypeArguments(ProducerRecord.class, String.class,
                String.class)) {
            return (LocalReturnBinder) (event, sent) -> sent.record();
        } else if (reflected.isReturnTypeParameterizedWithTypeArguments(CompletableFuture.class,
                RecordMetadata.class)) {
            return (LocalReturnBinder) (event, sent) -> sent.future();
        }

        throw new UnsupportedOperationException("Un-supported return type on method " + reflected.method());
    }

    private <T> T wrapGet(final CompletableFuture<T> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ProxyReturnBindingException(e);
        }
    }
}
