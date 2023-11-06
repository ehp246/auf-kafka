package me.ehp246.aufkafka.core.producer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import me.ehp246.aufkafka.api.annotation.OfCorrelationId;
import me.ehp246.aufkafka.api.annotation.OfHeader;
import me.ehp246.aufkafka.api.annotation.OfKey;
import me.ehp246.aufkafka.api.producer.ProxyMethodParser;
import me.ehp246.aufkafka.api.spi.PropertyResolver;

/**
 * @author Lei Yang
 *
 */
public final class DefaultProxyMethodParser implements ProxyMethodParser {
    private final static Set<Class<? extends Annotation>> PARAMETER_ANNOTATIONS = Set.of(OfKey.class, OfHeader.class,
            OfCorrelationId.class);

    private final PropertyResolver propertyResolver;

    DefaultProxyMethodParser(final PropertyResolver propertyResolver) {
        this.propertyResolver = propertyResolver;
    }

    @Override
    public Parsed parse(final Method method) {
        return new Parsed(null);
    }

}
