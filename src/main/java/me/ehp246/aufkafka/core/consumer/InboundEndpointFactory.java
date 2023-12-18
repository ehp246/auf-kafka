package me.ehp246.aufkafka.core.consumer;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.consumer.InboundEndpoint;
import me.ehp246.aufkafka.api.consumer.InvocableKeyRegistry;
import me.ehp246.aufkafka.api.consumer.InvocableScanner;
import me.ehp246.aufkafka.api.consumer.InvocationListener;
import me.ehp246.aufkafka.api.consumer.MsgConsumer;
import me.ehp246.aufkafka.api.spi.PropertyResolver;
import me.ehp246.aufkafka.core.util.OneUtil;

/**
 * @author Lei Yang
 * @since 1.0
 * @see AnnotatedInboundConsumerRegistrar
 * @see EnableForKafka
 */
public final class InboundEndpointFactory {
    private final PropertyResolver propertyResolver;
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;
    private final InvocableScanner invocableScanner;

    public InboundEndpointFactory(final AutowireCapableBeanFactory autowireCapableBeanFactory,
            final PropertyResolver propertyResolver, final InvocableScanner invocableScanner) {
        super();
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
        this.propertyResolver = propertyResolver;
        this.invocableScanner = invocableScanner;
    }

    @SuppressWarnings("unchecked")
    public InboundEndpoint newInstance(final Map<String, Object> inboundAttributes,
            final Set<String> scanPackages, final String beanName,
            final String defaultConsumerName) {
        final var defaultConsumer = Optional.ofNullable(defaultConsumerName)
                .map(propertyResolver::apply).filter(OneUtil::hasValue)
                .map(name -> autowireCapableBeanFactory.getBean(name, MsgConsumer.class))
                .orElse(null);

        final var fromAttribute = (Map<String, Object>) inboundAttributes.get("value");

        final boolean autoStartup = Boolean.parseBoolean(
                propertyResolver.apply(inboundAttributes.get("autoStartup").toString()));

        final InboundEndpoint.From from = new InboundEndpoint.From() {
            private final String topic = propertyResolver
                    .apply(fromAttribute.get("value").toString());

            @Override
            public String topic() {
                return topic;
            }
        };

        final var registery = new DefaultInvocableKeyRegistry()
                .register(
                        this.invocableScanner
                                .apply(Arrays.asList((Class<?>[]) inboundAttributes.get("register"))
                                        .stream().collect(Collectors.toSet()), scanPackages)
                                .stream());

        final var invocationListener = Optional
                .ofNullable(inboundAttributes.get("invocationListener").toString())
                .map(propertyResolver::apply).filter(OneUtil::hasValue)
                .map(name -> autowireCapableBeanFactory.getBean(name, InvocationListener.class))
                .orElse(null);

        return new InboundEndpoint() {

            @Override
            public InvocableKeyRegistry keyRegistry() {
                return registery;
            }

            @Override
            public String name() {
                return beanName;
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
            public MsgConsumer defaultConsumer() {
                return defaultConsumer;
            }

            @Override
            public From from() {
                return from;
            }
        };
    }
}
