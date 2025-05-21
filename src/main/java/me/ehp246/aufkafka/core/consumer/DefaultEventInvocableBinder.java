package me.ehp246.aufkafka.core.consumer;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
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
import me.ehp246.aufkafka.api.annotation.OfMDC;
import me.ehp246.aufkafka.api.annotation.OfPartition;
import me.ehp246.aufkafka.api.annotation.OfValue;
import me.ehp246.aufkafka.api.consumer.BoundInvocable;
import me.ehp246.aufkafka.api.consumer.EventInvocable;
import me.ehp246.aufkafka.api.consumer.EventInvocableBinder;
import me.ehp246.aufkafka.api.consumer.InboundEvent;
import me.ehp246.aufkafka.api.consumer.InboundEvent;
import me.ehp246.aufkafka.api.exception.UnboundParameterException;
import me.ehp246.aufkafka.api.serializer.json.FromJson;
import me.ehp246.aufkafka.api.serializer.json.JacksonObjectOfBuilder;
import me.ehp246.aufkafka.core.reflection.ReflectedMethod;
import me.ehp246.aufkafka.core.reflection.ReflectedParameter;
import me.ehp246.aufkafka.core.reflection.ReflectedType;
import me.ehp246.aufkafka.core.util.OneUtil;

/**
 * The binding involves creating the arguments and inject values from the event.
 * 
 * @author Lei Yang
 * @since 1.0
 */
public final class DefaultEventInvocableBinder implements EventInvocableBinder {
    private static final Map<Class<? extends Annotation>, Function<InboundEvent, Object>> HEADER_VALUE_SUPPLIERS = Map
            .of(OfKey.class, InboundEvent::key, OfPartition.class, InboundEvent::partition);

    private static final Set<Class<? extends Annotation>> PROPERTY_ANNOTATIONS = Set
            .copyOf(HEADER_VALUE_SUPPLIERS.keySet());

    private final FromJson fromJson;
    private final Map<Method, EventBinders> parsed = new ConcurrentHashMap<>();

    public DefaultEventInvocableBinder(final FromJson fromJson) {
        super();
        this.fromJson = fromJson;
    }

    @Override
    public BoundInvocable bind(final EventInvocable target, final InboundEvent event) {
        final var method = target.method();

        final var argBinders = this.parsed.computeIfAbsent(method, this::parse);

        final var paramBinders = argBinders.eventBinders();
        final var parameterCount = method.getParameterCount();

        /*
         * Bind the arguments.
         */
        final var arguments = new Object[parameterCount];
        for (int i = 0; i < parameterCount; i++) {
            arguments[i] = paramBinders.get(i).apply(event);
        }

        /*
         * Bind the MDC map
         */
        final var mdcMapBinders = argBinders.mdcMapBinders();
        final Map<String, String> mdcMap = new HashMap<>();
        if (mdcMapBinders != null && mdcMapBinders.size() > 0) {
            mdcMapBinders.entrySet().stream().forEach(entry -> {
                mdcMap.put(entry.getKey(), mdcMapBinders.get(entry.getKey()).apply(arguments));
            });
        }

        return new BoundInvocable() {

            @Override
            public EventInvocable eventInvocable() {
                return target;
            }

            @Override
            public InboundEvent event() {
                return event;
            }

            @Override
            public Object[] arguments() {
                return arguments;
            }

            @Override
            public Map<String, String> mdcMap() {
                return mdcMap;
            }

        };
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private EventBinders parse(final Method method) {
        final var parameters = method.getParameters();
        final Map<Integer, Function<InboundEvent, Object>> paramBinders = new HashMap<>();
        final var valueParamRef = new ReflectedParameter[] { null };

        for (int i = 0; i < parameters.length; i++) {
            final var parameter = parameters[i];
            final var type = parameter.getType();

            /*
             * Bindings in descending priorities.
             */
            if (type.isAssignableFrom(InboundEvent.class)) {
                paramBinders.put(i, event -> event);
                continue;
            } else if (type.isAssignableFrom(ConsumerRecord.class)) {
                paramBinders.put(i, InboundEvent::consumerRecord);
                continue;
            } else if (type.isAssignableFrom(FromJson.class)) {
                paramBinders.put(i, event -> fromJson);
                continue;
            } else if (type.isAssignableFrom(Headers.class)) {
                paramBinders.put(i, InboundEvent::headers);
                continue;
            } else if (type.isAssignableFrom(Header.class)) {
                final var key = OneUtil.getIfBlank(Optional.ofNullable(parameter.getAnnotation(OfHeader.class)).map(OfHeader::value).orElse(null),
                        () -> OneUtil.firstUpper(parameter.getName()));
                
                paramBinders.put(i, event -> event.headers().lastHeader(key));
                continue;
            }
            /*
             * Annotated properties.
             */
            final var annotations = parameter.getAnnotations();
            final var propertyAnnotation = Stream.of(annotations)
                    .filter(annotation -> PROPERTY_ANNOTATIONS.contains(annotation.annotationType())).findAny();
            if (propertyAnnotation.isPresent()) {
                final var fn = HEADER_VALUE_SUPPLIERS.get(propertyAnnotation.get().annotationType());
                paramBinders.put(i, fn::apply);
                continue;
            }

            /*
             * Headers
             */
            final var headerAnnotation = Stream.of(annotations).filter(OfHeader.class::isInstance).findAny();
            if (headerAnnotation.isPresent()) {
                final var key = OneUtil.getIfBlank(parameter.getAnnotation(OfHeader.class).value(),
                        () -> OneUtil.firstUpper(parameter.getName()));

                if (type.isAssignableFrom(Iterable.class)) {
                    paramBinders.put(i, event -> event.headers().headers(key));
                    continue;
                } else if (type.isAssignableFrom(List.class)) {
                    paramBinders.put(i, event -> event.headerValues(key));
                    continue;
                } else if (type.isAssignableFrom(Map.class)) {
                    paramBinders.put(i, InboundEvent::headerMap);
                    continue;
                } else if (type.isAssignableFrom(String.class)) {
                    paramBinders.put(i, event -> event.lastHeader(key).orElse(null));
                    continue;
                } else if (type.isAssignableFrom(Boolean.class) || type.isAssignableFrom(boolean.class)) {
                    paramBinders.put(i, event -> event.lastHeader(key, Boolean::valueOf));
                    continue;
                } else if (type.isAssignableFrom(Byte.class) || type.isAssignableFrom(byte.class)) {
                    paramBinders.put(i, event -> event.lastHeader(key, Byte::valueOf));
                    continue;
                } else if (type.isAssignableFrom(Short.class) || type.isAssignableFrom(short.class)) {
                    paramBinders.put(i, event -> event.lastHeader(key, Short::valueOf));
                    continue;
                } else if (type.isAssignableFrom(Integer.class) || type.isAssignableFrom(int.class)) {
                    paramBinders.put(i, event -> event.lastHeader(key, Integer::valueOf));
                    continue;
                } else if (type.isAssignableFrom(Long.class) || type.isAssignableFrom(long.class)) {
                    paramBinders.put(i, event -> event.lastHeader(key, Long::valueOf));
                    continue;
                } else if (type.isAssignableFrom(Double.class) || type.isAssignableFrom(double.class)) {
                    paramBinders.put(i, event -> event.lastHeader(key, Double::valueOf));
                    continue;
                } else if (type.isAssignableFrom(Float.class) || type.isAssignableFrom(float.class)) {
                    paramBinders.put(i, event -> event.lastHeader(key, Float::valueOf));
                    continue;
                } else if (type.isAssignableFrom(Instant.class)) {
                    paramBinders.put(i, event -> event.lastHeader(key, Instant::parse));
                    continue;
                } else if (type.isAssignableFrom(UUID.class)) {
                    paramBinders.put(i, event -> event.lastHeader(key, UUID::fromString));
                    continue;
                } else if (type.isEnum()) {
                    paramBinders.put(i, event -> event.lastHeader(key, str -> Enum.valueOf((Class<Enum>) type, str)));
                    continue;
                }
                throw new RuntimeException("Un-supported " + OfHeader.class.getSimpleName() + " parameter type: "
                        + parameter + " on " + method);
            }

            /*
             * Value
             */
            final var ofValueAnnotation = Stream.of(annotations).filter(OfValue.class::isInstance).findAny();
            if (ofValueAnnotation.isPresent()) {
                final var bodyOf = JacksonObjectOfBuilder
                        .ofView(Optional.ofNullable(parameter.getAnnotation(JsonView.class)).map(JsonView::value)
                                .map(OneUtil::firstOrNull).orElse(null), parameter.getType());

                paramBinders.put(i, msg -> msg.value() == null ? null : fromJson.apply(msg.value(), bodyOf));
                valueParamRef[0] = new ReflectedParameter(parameters[i], i);

                continue;
            }

            throw new UnboundParameterException(parameter, method);
        }

        /*
         * Parameters, then the value.
         */
        final var mdcMapBinders = new HashMap<String, Function<Object[], String>>();

        mdcMapBinders.putAll(new ReflectedMethod(method).allParametersWith(OfMDC.class).stream()
                .filter(p -> p.parameter().getAnnotation(OfMDC.class).op() == OfMDC.Op.Default)
                .collect(Collectors.toMap(p -> {
                    final var name = p.parameter().getAnnotation(OfMDC.class).value();
                    return OneUtil.hasValue(name) ? name : p.parameter().getName();
                }, p -> {
                    final var index = p.index();
                    return (Function<Object[], String>) (args -> args[index] == null ? null : args[index] + "");
                }, (l, r) -> r)));

        /*
         * Assume only one annotated value parameter on the parameter list
         */
        final var valueReflectedParam = valueParamRef[0];

        if (valueReflectedParam == null || valueReflectedParam.parameter().getAnnotation(OfMDC.class) == null) {
            return new EventBinders(paramBinders, mdcMapBinders);
        }

        /*
         * Work on the context from the value.
         */
        final var valueParam = valueReflectedParam.parameter();
        final var valueParamIndex = valueReflectedParam.index();
        final var ofMDC = valueParam.getAnnotation(OfMDC.class);

        switch (ofMDC.op()) {
        case Introspect:
            /*
             * Duplicated names will overwrite each other un-deterministically.
             */
            final var bodyParamContextName = ofMDC.value();
            final var bodyFieldBinders = new ReflectedType<>(valueParam.getType()).streamSuppliersWith(OfMDC.class)
                    .filter(m -> m.getAnnotation(OfMDC.class).op() == OfMDC.Op.Default)
                    .collect(
                            Collectors.toMap(
                                    m -> bodyParamContextName + Optional.of(m.getAnnotation(OfMDC.class).value())
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
                            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        };
                    }));
            mdcMapBinders.putAll(bodyFieldBinders);
            break;
        default:
            mdcMapBinders.put(
                    Optional.ofNullable(valueParam.getAnnotation(OfMDC.class)).map(OfMDC::value)
                            .filter(OneUtil::hasValue).orElseGet(valueParam::getName),
                    args -> args[valueParamIndex] == null ? null : args[valueParamIndex] + "");
            break;
        }

        return new EventBinders(paramBinders, mdcMapBinders);
    }

    private record EventBinders(Map<Integer, Function<InboundEvent, Object>> eventBinders,
            Map<String, Function<Object[], String>> mdcMapBinders) {
    };
}
