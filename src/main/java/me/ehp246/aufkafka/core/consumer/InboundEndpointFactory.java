package me.ehp246.aufkafka.core.consumer;

import java.beans.ExceptionListener;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.ErrorHandler;

import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.consumer.InboundEndpoint;
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

    public InboundEndpointFactory(final AutowireCapableBeanFactory autowireCapableBeanFactory,
            final PropertyResolver propertyResolver) {
        super();
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
        this.propertyResolver = propertyResolver;
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

        final var subAttribute = (Map<String, Object>) fromAttribute.get("sub");

        final InboundEndpoint.From from = new InboundEndpoint.From() {
            private final String topic = propertyResolver
                    .apply(fromAttribute.get("value").toString());

            @Override
            public String topic() {
                return topic;
            }
        };

        final var invocationListener = Optional
                .ofNullable(inboundAttributes.get("invocationListener").toString())
                .map(propertyResolver::apply).filter(OneUtil::hasValue)
                .map(name -> autowireCapableBeanFactory.getBean(name, InvocationListener.class))
                .orElse(null);

        final var errorHandler = Optional
                .ofNullable(inboundAttributes.get("errorHandler").toString())
                .map(propertyResolver::apply).filter(OneUtil::hasValue)
                .map(name -> autowireCapableBeanFactory.getBean(name, ErrorHandler.class))
                .orElse(null);

        final var exceptionListener = Optional
                .ofNullable(inboundAttributes.get("exceptionListener").toString())
                .map(propertyResolver::apply).filter(OneUtil::hasValue)
                .map(name -> autowireCapableBeanFactory.getBean(name, ExceptionListener.class))
                .orElse(null);

        return new InboundEndpoint() {

            @Override
            public From from() {
                return from;
            }
        };
    }
}
