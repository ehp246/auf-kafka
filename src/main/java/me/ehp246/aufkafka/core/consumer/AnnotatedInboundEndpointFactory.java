package me.ehp246.aufkafka.core.consumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.consumer.DispatchListener;
import me.ehp246.aufkafka.api.consumer.EndpointAt;
import me.ehp246.aufkafka.api.consumer.EventInvocableRegistry;
import me.ehp246.aufkafka.api.consumer.InboundEndpoint;
import me.ehp246.aufkafka.api.consumer.InvocableScanner;
import me.ehp246.aufkafka.api.consumer.InvocationListener;
import me.ehp246.aufkafka.api.spi.ExpressionResolver;
import me.ehp246.aufkafka.core.util.OneUtil;

/**
 * @author Lei Yang
 * @since 1.0
 * @see AnnotatedInboundEndpointRegistrar
 * @see AnnotatedInboundEndpointConsumerConfigurer
 * @see EnableForKafka
 * 
 */
public final class AnnotatedInboundEndpointFactory {
    private final ExpressionResolver expressionResolver;
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;
    private final InvocableScanner invocableScanner;

    public AnnotatedInboundEndpointFactory(final AutowireCapableBeanFactory autowireCapableBeanFactory,
            final ExpressionResolver expressionResolver, final InvocableScanner invocableScanner) {
        super();
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
        this.expressionResolver = expressionResolver;
        this.invocableScanner = invocableScanner;
    }

    @SuppressWarnings("unchecked")
    public InboundEndpoint newInstance(final Map<String, Object> inboundAttributes, final Set<String> scanPackages,
            final String beanName) {
        final var fromAttribute = (Map<String, Object>) inboundAttributes.get("value");

        final var configName = inboundAttributes.get("configName").toString();

        final var unknowListener = Optional.ofNullable(inboundAttributes.get("unknownEventListener").toString())
                .map(expressionResolver::apply).filter(OneUtil::hasValue)
                .map(name -> autowireCapableBeanFactory.getBean(name, DispatchListener.UnknownEventListener.class))
                .orElse(null);

        final var exceptionListener = Optional.ofNullable(inboundAttributes.get("dispatchExceptionListener").toString())
                .map(expressionResolver::apply).filter(OneUtil::hasValue)
                .map(name -> autowireCapableBeanFactory.getBean(name, DispatchListener.ExceptionListener.class))
                .orElse(null);

        final var consumerProperties = consumerProperties(
                Arrays.asList((String[]) inboundAttributes.get("consumerProperties")), beanName);

        final var autoStartup = Boolean
                .parseBoolean(expressionResolver.apply(inboundAttributes.get("autoStartup").toString()));

        final var pollDuration = Duration
                .parse(expressionResolver.apply(inboundAttributes.get("pollDuration").toString()));

        final EndpointAt from = new EndpointAt(expressionResolver.apply(fromAttribute.get("value").toString()),
                Arrays.stream((String[]) fromAttribute.get("partitions")).map(expressionResolver::apply)
                        .filter(OneUtil::hasValue).flatMap(OneUtil::parseIntegerRange).sorted().distinct().toList());

        final var registery = new DefaultEventInvocableRegistry(inboundAttributes.get("eventHeader").toString());

        this.invocableScanner.apply(
                Arrays.asList((Class<?>[]) inboundAttributes.get("register")).stream().collect(Collectors.toSet()),
                scanPackages).entrySet().stream().forEach(entry -> {
                    final var key = entry.getKey();
                    entry.getValue().stream().forEach(value -> registery.register(key, value));
                });

        final var invocationListener = Optional.ofNullable(inboundAttributes.get("invocationListener").toString())
                .map(expressionResolver::apply).filter(OneUtil::hasValue)
                .map(name -> autowireCapableBeanFactory.getBean(name, InvocationListener.class)).orElse(null);

        return new InboundEndpoint() {
            @Override
            public EndpointAt at() {
                return from;
            }

            @Override
            public EventInvocableRegistry invocableRegistry() {
                return registery;
            }

            @Override
            public String name() {
                return beanName;
            }

            @Override
            public String configName() {
                return configName;
            }

            @Override
            public Map<String, Object> consumerProperties() {
                return consumerProperties;
            }

            @Override
            public boolean autoStartup() {
                return autoStartup;
            }

            @Override
            public InvocationListener invocationListener() {
                return invocationListener;
            }

            @Override
            public DispatchListener.UnknownEventListener unknownEventListener() {
                return unknowListener;
            }

            @Override
            public DispatchListener.ExceptionListener dispatchExceptionListener() {
                return exceptionListener;
            }

            @Override
            public Duration pollDuration() {
                return pollDuration;
            }
        };
    }

    private Map<String, Object> consumerProperties(final List<String> properties, final String beanName) {
        if ((properties.size() & 1) != 0) {
            throw new IllegalArgumentException(
                    "Consumer properties should be in name/value pair on '" + beanName + "'");
        }

        final Map<String, Object> resolvedProperties = new HashMap<>();
        for (int i = 0; i < properties.size(); i += 2) {
            resolvedProperties.put(properties.get(i), expressionResolver.apply(properties.get(i + 1)));
        }
        return resolvedProperties;
    }
}
