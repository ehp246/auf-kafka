package me.ehp246.aufkafka.core.consumer;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufkafka.api.annotation.OfKey;
import me.ehp246.aufkafka.api.annotation.OfLog4jContext;
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
            .of(OfKey.class, ConsumerRecord::key);

    private static final Set<Class<? extends Annotation>> HEADER_ANNOTATIONS = Set
            .copyOf(HEADER_VALUE_SUPPLIERS.keySet());

    private final FromJson fromJson;
    private final Map<Method, ArgBinders> parsed = new ConcurrentHashMap<>();

    public DefaultInvocableBinder(final FromJson fromJson) {
        super();
        this.fromJson = fromJson;
    }

    @Override
    public BoundInvocable bind(final Invocable target, final ConsumerRecord<String, String> msg) {
        final var method = target.method();

        final var argBinders = this.parsed.computeIfAbsent(method, this::parse);

        final var paramBinders = argBinders.paramBinders();
        final var parameterCount = method.getParameterCount();

        /*
         * Bind the arguments.
         */
        final var arguments = new Object[parameterCount];
        for (int i = 0; i < parameterCount; i++) {
            arguments[i] = paramBinders.get(i).apply(msg);
        }

        /*
         * Bind the Thread Context
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

    private ArgBinders parse(final Method method) {
        method.setAccessible(true);

        final var parameters = method.getParameters();
        final Map<Integer, Function<ConsumerRecord<String, String>, Object>> paramBinders = new HashMap<>();
        final var valueParamRef = new ReflectedParameter[] { null };

        for (int i = 0; i < parameters.length; i++) {
            final var parameter = parameters[i];
            final var type = parameter.getType();

            /*
             * Binding in descending priorities.
             */
            if (type.isAssignableFrom(ConsumerRecord.class)) {
                paramBinders.put(i, msg -> msg);
                continue;
            } else if (type.isAssignableFrom(FromJson.class)) {
                paramBinders.put(i, msg -> fromJson);
                continue;
            }

            /*
             * Headers.
             */
            final var annotations = parameter.getAnnotations();
            final var header = Stream.of(annotations)
                    .filter(annotation -> HEADER_ANNOTATIONS.contains(annotation.annotationType()))
                    .findAny();
            if (header.isPresent()) {
                final var fn = HEADER_VALUE_SUPPLIERS.get(header.get().annotationType());
                paramBinders.put(i, msg -> fn.apply(msg));
                continue;
            }

            /*
             * Body
             */
            final var bodyOf = JacksonObjectOfBuilder.ofView(
                    Optional.ofNullable(parameter.getAnnotation(JsonView.class))
                            .map(JsonView::value).map(OneUtil::firstOrNull).orElse(null),
                    parameter.getType());

            paramBinders.put(i,
                    msg -> msg.value() == null ? null : fromJson.apply(msg.value(), bodyOf));
            valueParamRef[0] = new ReflectedParameter(parameters[i], i);
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
            return new ArgBinders(paramBinders, log4jContextBinders);
        }

        /*
         * Work on the value.
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
                log4jContextBinders.put(
                        Optional.ofNullable(valueParam.getAnnotation(OfLog4jContext.class))
                                .map(OfLog4jContext::value).filter(OneUtil::hasValue)
                                .orElseGet(valueParam::getName),
                        args -> args[valueParamIndex] == null ? null : args[valueParamIndex] + "");
                break;
        }

        return new ArgBinders(paramBinders, log4jContextBinders);
    }

    record ArgBinders(Map<Integer, Function<ConsumerRecord<String, String>, Object>> paramBinders,
            Map<String, Function<Object[], String>> log4jContextBinders) {
    };
}
