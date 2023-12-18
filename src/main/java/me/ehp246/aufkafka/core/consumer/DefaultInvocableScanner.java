package me.ehp246.aufkafka.core.consumer;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import me.ehp246.aufkafka.api.annotation.Applying;
import me.ehp246.aufkafka.api.annotation.ForKey;
import me.ehp246.aufkafka.api.consumer.InstanceScope;
import me.ehp246.aufkafka.api.consumer.InvocableKeyDefinition;
import me.ehp246.aufkafka.api.consumer.InvocableScanner;
import me.ehp246.aufkafka.api.spi.PropertyResolver;
import me.ehp246.aufkafka.core.reflection.ReflectedType;
import me.ehp246.aufkafka.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
public final class DefaultInvocableScanner implements InvocableScanner {
    private final static Logger LOGGER = LogManager.getLogger();

    private final PropertyResolver propertyResolver;

    public DefaultInvocableScanner(final PropertyResolver propertyResolver) {
        super();
        this.propertyResolver = propertyResolver;
    }

    @Override
    public Set<InvocableKeyDefinition> apply(final Set<Class<?>> registering,
            final Set<String> scanPackages) {
        final var scanner = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(final AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isIndependent()
                        || beanDefinition.getMetadata().isInterface();
            }
        };
        scanner.addIncludeFilter(new AnnotationTypeFilter(ForKey.class));

        return OneUtil.streamOfNonNull(scanPackages).map(scanner::findCandidateComponents)
                .flatMap(Set::stream).map(bean -> {
                    try {
                        return Class.forName(bean.getBeanClassName());
                    } catch (final ClassNotFoundException e) {
                        LOGGER.atError().withThrowable(e).log("This should not happen: {}",
                                e::getMessage);
                    }
                    return null;
                }).filter(Objects::nonNull).map(this::newDefinition).filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private InvocableKeyDefinition newDefinition(final Class<?> type) {
        final var annotation = type.getAnnotation(ForKey.class);
        if (annotation == null) {
            return null;
        }

        if ((Modifier.isAbstract(type.getModifiers())
                && annotation.scope().equals(InstanceScope.MESSAGE)) || type.isEnum()) {
            throw new IllegalArgumentException("Un-instantiable type " + type.getName());
        }

        final var msgTypes = Arrays
                .asList(annotation.value().length == 0 ? new String[] { type.getSimpleName() }
                        : annotation.value())
                .stream().map(this.propertyResolver::apply)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream().map(entry -> {
                    if (entry.getValue() > 1) {
                        throw new IllegalArgumentException("Duplicate type '" + entry.getKey()
                                + "' on " + type.getCanonicalName());
                    }
                    return entry.getKey();
                }).collect(Collectors.toSet());

        final var invokings = new HashMap<String, Method>();
        final var reflected = new ReflectedType<>(type);

        // TODO: No invoking name anymore. Need to fix.
        // Search for the annotation first
        for (final var method : reflected.findMethods(Applying.class)) {
            invokings.put("", method);
        }

        /*
         * Look for 'apply'.
         */
        if (invokings.get("") == null) {
            final var invokes = reflected.findMethods("invoke");
            if (invokes.size() == 1) {
                invokings.put("", invokes.get(0));
            } else {
                final var applies = reflected.findMethods("apply");
                if (applies.size() == 1) {
                    invokings.put("", applies.get(0));
                }
            }
        }

        // There should be at least one method.
        if (invokings.get("") == null) {
            throw new IllegalArgumentException("No invocation method defined by " + type.getName());
        }

        return new InvocableKeyDefinition(msgTypes, type, Map.copyOf(invokings), annotation.scope(),
                annotation.invocation());
    }
}
