package me.ehp246.aufkafka.core.consumer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import me.ehp246.aufkafka.api.annotation.Applying;
import me.ehp246.aufkafka.api.annotation.Execution;
import me.ehp246.aufkafka.api.annotation.ForEvent;
import me.ehp246.aufkafka.api.annotation.ForKey;
import me.ehp246.aufkafka.api.consumer.EventInvocableDefinition;
import me.ehp246.aufkafka.api.consumer.EventInvocableLookupType;
import me.ehp246.aufkafka.api.consumer.EventInvocableRegistry;
import me.ehp246.aufkafka.api.consumer.InstanceScope;
import me.ehp246.aufkafka.api.consumer.InvocableScanner;
import me.ehp246.aufkafka.api.spi.ExpressionResolver;
import me.ehp246.aufkafka.core.reflection.ReflectedType;
import me.ehp246.aufkafka.core.util.OneUtil;

/**
 * Scans for {@linkplain ForKey} and {@linkplain ForEvent} classes.
 * <p>
 * Duplicate {@linkplain EventInvocableDefinition#names()} are accepted.
 * Collision detection on the lookup keys is implemented by
 * {@linkplain EventInvocableRegistry}.
 * 
 * @author Lei Yang
 * @see DefaultEventInvocableRegistry
 */
public final class DefaultInvocableScanner implements InvocableScanner {
    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultInvocableScanner.class);
    private final Map<Class<? extends Annotation>, EventInvocableLookupType> ANNO_KEYTYPE_MAP = Map.of(ForEvent.class,
            EventInvocableLookupType.EVENT_HEADER, ForKey.class, EventInvocableLookupType.KEY);

    private final ExpressionResolver expressionResolver;

    public DefaultInvocableScanner(final ExpressionResolver expressionResolver) {
        super();
        this.expressionResolver = expressionResolver;
    }

    @Override
    public Map<EventInvocableLookupType, Set<EventInvocableDefinition>> apply(final Set<Class<?>> registering,
            final Set<String> scanPackages) {
        final var map = new HashMap<EventInvocableLookupType, Set<EventInvocableDefinition>>();

        if (registering != null && !registering.isEmpty()) {
            for (final var type : registering) {
                final var annos = new HashSet<>(
                        Arrays.stream(type.getDeclaredAnnotations()).map(Annotation::annotationType).toList());
                annos.retainAll(ANNO_KEYTYPE_MAP.keySet());
                if (annos.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Missing required annotations from " + ANNO_KEYTYPE_MAP.keySet() + " on " + type.getName());
                }

                this.newDefinition(type).entrySet().stream().forEach(
                        entry -> map.computeIfAbsent(entry.getKey(), key -> new HashSet<>()).add(entry.getValue()));
            }
        }

        final var scanner = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(final AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isIndependent() || beanDefinition.getMetadata().isInterface();
            }
        };

        ANNO_KEYTYPE_MAP.keySet().stream().forEach(anno -> scanner.addIncludeFilter(new AnnotationTypeFilter(anno)));

        OneUtil.streamOfNonNull(scanPackages).map(scanner::findCandidateComponents).flatMap(Set::stream).map(bean -> {
            try {
                return Class.forName(bean.getBeanClassName());
            } catch (final ClassNotFoundException e) {
                LOGGER.atError().setMessage("This should not happen: {}").setCause(e).addArgument(e::getMessage).log();
            }
            return null;
        }).map(this::newDefinition).map(Map::entrySet).flatMap(Set::stream)
                .forEach(entry -> map.computeIfAbsent(entry.getKey(), key -> new HashSet<>()).add(entry.getValue()));

        /*
         * Locks down the set.
         */
        map.keySet().stream().forEach(key -> map.put(key, Collections.unmodifiableSet(map.get(key))));

        /*
         * Locks down the map.
         */
        return Collections.unmodifiableMap(map);
    }

    /**
     * Multiple annotations can be applied.
     * 
     * @param type
     * @return
     */
    private Map<EventInvocableLookupType, EventInvocableDefinition> newDefinition(final Class<?> type) {
        final var map = new HashMap<EventInvocableLookupType, EventInvocableDefinition>();

        for (final var annoType : ANNO_KEYTYPE_MAP.keySet()) {
            final var anno = type.getAnnotation(annoType);
            if (anno == null) {
                continue;
            }

            final String[] value;
            final Execution execution;

            switch (ANNO_KEYTYPE_MAP.get(annoType)) {
            case EVENT_HEADER:
                final var forEventType = (ForEvent) anno;
                value = forEventType.value();
                execution = forEventType.execution();
                break;
            case KEY:
                final var forKey = (ForKey) anno;
                value = forKey.value();
                execution = forKey.execution();
                break;
            default:
                throw new IllegalArgumentException("This should not happen: type " + type.getCanonicalName() + " with "
                        + annoType.getCanonicalName());
            }

            if (!Modifier.isPublic(type.getModifiers())) {
                throw new IllegalArgumentException("public modifier required on " + type.getName());
            }
            if ((Modifier.isAbstract(type.getModifiers()) && execution.scope().equals(InstanceScope.EVENT))
                    || type.isEnum()) {
                throw new IllegalArgumentException("Un-instantiable type " + type.getName());
            }

            final var lookupKeys = Arrays.asList(value.length == 0 ? new String[] { type.getSimpleName() } : value)
                    .stream().map(this.expressionResolver::apply)
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream()
                    .map(entry -> {
                        if (entry.getValue() > 1) {
                            throw new IllegalArgumentException(
                                    "Duplicate type '" + entry.getKey() + "' on " + type.getCanonicalName());
                        }
                        return entry.getKey();
                    }).collect(Collectors.toSet());

            final var invokings = new HashMap<String, Method>();
            final var reflected = new ReflectedType<>(type);

            /*
             * No invoking name for now. Search for the annotation first.
             */
            invokings.put("", reflected.findSingleAnnotatedMethod(Applying.class));

            /*
             * Look for 'apply'.
             */
            if (invokings.get("") == null) {
                invokings.put("", reflected.findSingleNamedMethod("apply"));
            }

            /*
             * There should be at least one method.
             */
            if (invokings.get("") == null) {
                throw new NoSuchElementException("No invocation method defined by " + type.getName());
            }

            map.put(ANNO_KEYTYPE_MAP.get(annoType), new EventInvocableDefinition(lookupKeys, type,
                    Map.copyOf(invokings), execution.scope(), execution.invocation()));
        }

        return map;
    }
}
