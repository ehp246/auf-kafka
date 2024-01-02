package me.ehp246.aufkafka.core.consumer;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufkafka.api.annotation.OfHeader;
import me.ehp246.aufkafka.api.annotation.OfKey;
import me.ehp246.aufkafka.api.annotation.OfLog4jContext;
import me.ehp246.aufkafka.api.annotation.OfPartition;
import me.ehp246.aufkafka.api.annotation.OfValue;
import me.ehp246.aufkafka.api.consumer.BoundInvocable;
import me.ehp246.aufkafka.api.consumer.Invocable;
import me.ehp246.aufkafka.api.consumer.InvocableBinder;
import me.ehp246.aufkafka.api.serializer.json.FromJson;
import me.ehp246.aufkafka.api.serializer.json.JacksonObjectOfBuilder;
import me.ehp246.aufkafka.core.reflection.ReflectedMethod;
import me.ehp246.aufkafka.core.reflection.ReflectedParameter;
import me.ehp246.aufkafka.core.reflection.ReflectedType;
import me.ehp246.aufkafka.core.util.OneUtil;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public final class DefaultInvocableBinder implements InvocableBinder {
    private static final Map<Class<? extends Annotation>, Function<ConsumerRecord<String, String>, Object>> HEADER_VALUE_SUPPLIERS = Map
            .of(OfKey.class, ConsumerRecord::key, OfPartition.class, ConsumerRecord::partition);

    private static final Set<Class<? extends Annotation>> PROPERTY_ANNOTATIONS = Set
            .copyOf(HEADER_VALUE_SUPPLIERS.keySet());

    private final FromJson fromJson;
    private final Map<Method, ConsumerRecordBinders> parsed = new ConcurrentHashMap<>();

    public DefaultInvocableBinder(final FromJson fromJson) {
        super();
        this.fromJson = fromJson;
    }

    @Override
    public BoundInvocable bind(final Invocable target, final ConsumerRecord<String, String> msg) {
        final var method = target.method();

        final var argBinders = this.parsed.computeIfAbsent(method, this::parse);

        final var paramBinders = argBinders.recordBinders();
        final var parameterCount = method.getParameterCount();

        /*
         * Bind the arguments.
         */
        final var arguments = new Object[parameterCount];
        for (int i = 0; i < parameterCount; i++) {
            arguments[i] = paramBinders.get(i).apply(msg);
        }

        /*
         * Bind the Log4j Context
         */
        final var log4jContextBinders = argBinders.log4jContextBinders();
        final Map<String, String> log4jContext = new HashMap<>();
        if (log4jContextBinders != null && log4jContextBinders.size() > 0) {
            log4jContextBinders.entrySet().stream().forEach(entry -> {
                log4jContext.put(entry.getKey(),
                        log4jContextBinders.get(entry.getKey()).apply(arguments));
            });
        }

        return new BoundInvocable() {

            @Override
            public Invocable invocable() {
                return target;
            }

            @Override
            public ConsumerRecord<String, String> msg() {
                return msg;
            }

            @Override
            public Object[] arguments() {
                return arguments;
            }

            @Override
            public Map<String, String> log4jContext() {
                return log4jContext;
            }

        };
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private ConsumerRecordBinders parse(final Method method) {
        method.setAccessible(true);

        final var parameters = method.getParameters();
        final Map<Integer, Function<ConsumerRecord<String, String>, Object>> paramBinders = new HashMap<>();
        final var valueParamRef = new ReflectedParameter[] { null };

        for (int i = 0; i < parameters.length; i++) {
            final var parameter = parameters[i];
            final var type = parameter.getType();

            /*
             * Bindings in descending priorities.
             */
            if (type.isAssignableFrom(ConsumerRecord.class)) {
                paramBinders.put(i, msg -> msg);
                continue;
            } else if (type.isAssignableFrom(FromJson.class)) {
                paramBinders.put(i, msg -> fromJson);
                continue;
            }

            /*
             * Annotated properties.
             */
            final var annotations = parameter.getAnnotations();
            final var propertyAnnotation = Stream.of(annotations).filter(
                    annotation -> PROPERTY_ANNOTATIONS.contains(annotation.annotationType()))
                    .findAny();
            if (propertyAnnotation.isPresent()) {
                final var fn = HEADER_VALUE_SUPPLIERS
                        .get(propertyAnnotation.get().annotationType());
                paramBinders.put(i, msg -> fn.apply(msg));
                continue;
            }

            /*
             * Headers
             */
            final var headerAnnotation = Stream.of(annotations).filter(OfHeader.class::isInstance)
                    .findAny();
            if (headerAnnotation.isPresent()) {
                final var key = OneUtil.getIfBlank(parameter.getAnnotation(OfHeader.class).value(),
                        () -> OneUtil.firstUpper(parameter.getName()));

                if (type.isAssignableFrom(Headers.class)) {
                    paramBinders.put(i, ConsumerRecord::headers);
                    continue;
                } else if (type.isAssignableFrom(Header.class)) {
                    paramBinders.put(i, msg -> msg.headers().lastHeader(key));
                    continue;
                } else if (type.isAssignableFrom(Iterable.class)) {
                    paramBinders.put(i, msg -> msg.headers().headers(key));
                    continue;
                } else if (type.isAssignableFrom(List.class)) {
                    paramBinders.put(i, msg -> OneUtil.toList(msg.headers().headers(key)));
                    continue;
                } else if (type.isAssignableFrom(Map.class)) {
                    paramBinders.put(i, msg -> {
                        return OneUtil.toList(msg.headers()).stream()
                                .collect(Collectors.toMap(Header::key, header -> {
                                    final var list = new ArrayList<String>();
                                    list.add(new String(header.value(), StandardCharsets.UTF_8));
                                    return list;
                                }, (l, r) -> {
                                    l.addAll(r);
                                    return l;
                                }));
                    });
                    continue;
                } else if (type.isAssignableFrom(String.class)) {
                    paramBinders.put(i, msg -> {
                        final var header = msg.headers().lastHeader(key);
                        return header == null ? null : OneUtil.toString(header.value());
                    });
                    continue;
                } else if (type.isAssignableFrom(Boolean.class)
                        || type.isAssignableFrom(boolean.class)) {
                    paramBinders.put(i,
                            msg -> OneUtil.headerValue(msg.headers(), key, Boolean::valueOf));
                    continue;
                } else if (type.isAssignableFrom(Byte.class) || type.isAssignableFrom(byte.class)) {
                    paramBinders.put(i,
                            msg -> OneUtil.headerValue(msg.headers(), key, Byte::valueOf));
                    continue;
                } else if (type.isAssignableFrom(Short.class)
                        || type.isAssignableFrom(short.class)) {
                    paramBinders.put(i,
                            msg -> OneUtil.headerValue(msg.headers(), key, Short::valueOf));
                    continue;
                } else if (type.isAssignableFrom(Integer.class)
                        || type.isAssignableFrom(int.class)) {
                    paramBinders.put(i,
                            msg -> OneUtil.headerValue(msg.headers(), key, Integer::valueOf));
                    continue;
                } else if (type.isAssignableFrom(Long.class) || type.isAssignableFrom(long.class)) {
                    paramBinders.put(i,
                            msg -> OneUtil.headerValue(msg.headers(), key, Long::valueOf));
                    continue;
                } else if (type.isAssignableFrom(Double.class)
                        || type.isAssignableFrom(double.class)) {
                    paramBinders.put(i,
                            msg -> OneUtil.headerValue(msg.headers(), key, Double::valueOf));
                    continue;
                } else if (type.isAssignableFrom(Float.class)
                        || type.isAssignableFrom(float.class)) {
                    paramBinders.put(i,
                            msg -> OneUtil.headerValue(msg.headers(), key, Float::valueOf));
                    continue;
                } else if (type.isAssignableFrom(Instant.class)) {
                    paramBinders.put(i,
                            msg -> OneUtil.headerValue(msg.headers(), key, Instant::parse));
                    continue;
                } else if (type.isAssignableFrom(UUID.class)) {
                    paramBinders.put(i,
                            msg -> OneUtil.headerValue(msg.headers(), key, UUID::fromString));
                    continue;
                } else if (type.isEnum()) {
                    paramBinders.put(i, msg -> OneUtil.headerValue(msg.headers(), key,
                            str -> Enum.valueOf((Class<Enum>) type, str)));
                    continue;
                }
                throw new RuntimeException("Un-supported " + OfHeader.class.getSimpleName()
                        + " parameter type: " + parameter + " on " + method);
            }

            /*
             * Value
             */
            final var ofValueAnnotation = Stream.of(annotations).filter(OfValue.class::isInstance)
                    .findAny();
            if (ofValueAnnotation.isPresent()) {
                final var bodyOf = JacksonObjectOfBuilder.ofView(
                        Optional.ofNullable(parameter.getAnnotation(JsonView.class))
                                .map(JsonView::value).map(OneUtil::firstOrNull).orElse(null),
                        parameter.getType());

                paramBinders.put(i,
                        msg -> msg.value() == null ? null : fromJson.apply(msg.value(), bodyOf));
                valueParamRef[0] = new ReflectedParameter(parameters[i], i);

                continue;
            }
        }

        /*
         * Parameters, then the value.
         */
        final var log4jContextBinders = new HashMap<String, Function<Object[], String>>();

        log4jContextBinders.putAll(new ReflectedMethod(method)
                .allParametersWith(OfLog4jContext.class).stream().filter(p -> p.parameter()
                        .getAnnotation(OfLog4jContext.class).op() == OfLog4jContext.Op.Default)
                .collect(Collectors.toMap(p -> {
                    final var name = p.parameter().getAnnotation(OfLog4jContext.class).value();
                    return OneUtil.hasValue(name) ? name : p.parameter().getName();
                }, p -> {
                    final var index = p.index();
                    return (Function<Object[], String>) (args -> args[index] == null ? null
                            : args[index] + "");
                }, (l, r) -> r)));

        /*
         * Assume only one value parameter on the parameter list
         */
        final var valueReflectedParam = valueParamRef[0];

        if (valueReflectedParam == null
                || valueReflectedParam.parameter().getAnnotation(OfLog4jContext.class) == null) {
            return new ConsumerRecordBinders(paramBinders, log4jContextBinders);
        }

        /*
         * Work on the context from the value.
         */
        final var valueParam = valueReflectedParam.parameter();
        final var valueParamIndex = valueReflectedParam.index();
        final var ofLog4jContext = valueParam.getAnnotation(OfLog4jContext.class);

        switch (ofLog4jContext.op()) {
            case Introspect:
                /*
                 * Duplicated names will overwrite each other un-deterministically.
                 */
                final var bodyParamContextName = ofLog4jContext.value();
                final var bodyFieldBinders = new ReflectedType<>(valueParam.getType())
                        .streamSuppliersWith(OfLog4jContext.class).filter(
                                m -> m.getAnnotation(OfLog4jContext.class)
                                        .op() == OfLog4jContext.Op.Default)
                        .collect(Collectors.toMap(
                                m -> bodyParamContextName
                                        + Optional.of(m.getAnnotation(OfLog4jContext.class).value())
                                                .filter(OneUtil::hasValue).orElseGet(m::getName),
                                Function.identity(), (l, r) -> r))
                        .entrySet().stream().collect(Collectors.toMap(Entry::getKey, entry -> {
                            final var m = entry.getValue();
                            return (Function<Object[], String>) args -> {
                                final var body = args[valueParamIndex];
                                if (body == null) {
                                    return null;
                                }
                                try {
                                    final var returned = m.invoke(body);
                                    return returned == null ? null : returned + "";
                                } catch (IllegalAccessException | IllegalArgumentException
                                        | InvocationTargetException e) {
                                    throw new RuntimeException(e);
                                }
                            };
                        }));
                log4jContextBinders.putAll(bodyFieldBinders);
                break;
            default:
                log4jContextBinders.put(Optional
                        .ofNullable(valueParam.getAnnotation(OfLog4jContext.class))
                        .map(OfLog4jContext::value).filter(OneUtil::hasValue)
                        .orElseGet(valueParam::getName),
                        args -> args[valueParamIndex] == null ? null : args[valueParamIndex] + "");
                break;
        }

        return new ConsumerRecordBinders(paramBinders, log4jContextBinders);
    }

    record ConsumerRecordBinders(
            Map<Integer, Function<ConsumerRecord<String, String>, Object>> recordBinders,
            Map<String, Function<Object[], String>> log4jContextBinders) {
    };
}
