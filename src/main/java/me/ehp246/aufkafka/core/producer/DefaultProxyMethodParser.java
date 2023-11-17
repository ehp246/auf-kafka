package me.ehp246.aufkafka.core.producer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.function.Function;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfCorrelationId;
import me.ehp246.aufkafka.api.annotation.OfHeader;
import me.ehp246.aufkafka.api.annotation.OfKey;
import me.ehp246.aufkafka.api.annotation.OfPartition;
import me.ehp246.aufkafka.api.annotation.OfTopic;
import me.ehp246.aufkafka.api.producer.ProxyMethodParser;
import me.ehp246.aufkafka.api.spi.PropertyResolver;
import me.ehp246.aufkafka.core.reflection.ReflectedMethod;

/**
 * @author Lei Yang
 *
 */
public final class DefaultProxyMethodParser implements ProxyMethodParser {
    private final static Set<Class<? extends Annotation>> PARAMETER_ANNOTATIONS = Set.of(OfKey.class, OfPartition.class,
            OfHeader.class, OfCorrelationId.class);

    private final PropertyResolver propertyResolver;

    DefaultProxyMethodParser(final PropertyResolver propertyResolver) {
        this.propertyResolver = propertyResolver;
    }

    @Override
    public Parsed parse(final Method method) {
        final var reflected = new ReflectedMethod(method);

        final var topicBinder = reflected.allParametersWith(OfTopic.class).stream().findFirst()
                .map(p -> (Function<Object[], String>) args -> (String) args[p.index()])
                .orElseGet(() -> reflected.findOnMethodUp(ByKafka.class)
                        .map(an -> {
                            final var topic = propertyResolver.resolve(an.value());
                            return (Function<Object[], String>) args -> topic;
                        }).get());

        return new Parsed(new DefaultProxyInvocationBinder(topicBinder, null, null, null, null));
    }

}
