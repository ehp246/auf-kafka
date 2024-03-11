package me.ehp246.aufkafka.core.consumer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.api.consumer.ConsumerExceptionListener;
import me.ehp246.aufkafka.api.consumer.InboundEndpoint;
import me.ehp246.aufkafka.api.consumer.InvocableKeyRegistry;
import me.ehp246.aufkafka.api.consumer.InvocableScanner;
import me.ehp246.aufkafka.api.consumer.InvocationListener;
import me.ehp246.aufkafka.api.consumer.UnmatchedConsumer;
import me.ehp246.aufkafka.api.spi.PropertyResolver;
import me.ehp246.aufkafka.core.util.OneUtil;

/**
 * @author Lei Yang
 * @since 1.0
 * @see AnnotatedInboundEndpointRegistrar
 * @see InboundEndpointConsumerConfigurer
 * @see EnableForKafka
 * 
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
	public InboundEndpoint newInstance(final Map<String, Object> inboundAttributes, final Set<String> scanPackages,
			final String beanName) {
		final var consumerConfigName = inboundAttributes.get("consumerConfigName").toString();

		final var umatchedListener = Optional.ofNullable(inboundAttributes.get("unmatchedConsumer").toString())
				.map(propertyResolver::apply).filter(OneUtil::hasValue)
				.map(name -> autowireCapableBeanFactory.getBean(name, UnmatchedConsumer.class)).orElse(null);

		final var exceptionListener = Optional.ofNullable(inboundAttributes.get("consumerExceptionListener").toString())
				.map(propertyResolver::apply).filter(OneUtil::hasValue)
				.map(name -> autowireCapableBeanFactory.getBean(name, ConsumerExceptionListener.class)).orElse(null);

		final var consumerProperties = headerStatic(Arrays.asList((String[]) inboundAttributes.get("consumerProperties")),
				beanName);

		final var fromAttribute = (Map<String, Object>) inboundAttributes.get("value");

		final boolean autoStartup = Boolean
				.parseBoolean(propertyResolver.apply(inboundAttributes.get("autoStartup").toString()));

		final InboundEndpoint.From from = new InboundEndpoint.From() {
			private final String topic = propertyResolver.apply(fromAttribute.get("value").toString());

			@Override
			public String topic() {
				return topic;
			}
		};

		final var registery = new DefaultInvocableKeyRegistry().register(this.invocableScanner.apply(
				Arrays.asList((Class<?>[]) inboundAttributes.get("register")).stream().collect(Collectors.toSet()),
				scanPackages).stream());

		final var invocationListener = Optional.ofNullable(inboundAttributes.get("invocationListener").toString())
				.map(propertyResolver::apply).filter(OneUtil::hasValue)
				.map(name -> autowireCapableBeanFactory.getBean(name, InvocationListener.class)).orElse(null);

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
			public String consumerConfigName() {
				return consumerConfigName;
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
			public UnmatchedConsumer unmatchedConsumer() {
				return umatchedListener;
			}

			@Override
			public ConsumerExceptionListener consumerExceptionListener() {
				return exceptionListener;
			}

			@Override
			public From from() {
				return from;
			}
		};
	}

	private Map<String, Object> headerStatic(final List<String> properties, final String beanName) {
		if ((properties.size() & 1) != 0) {
			throw new IllegalArgumentException(
					"Consumer properties should be in name/value pair on '" + beanName + "'");
		}

		final Map<String, Object> resolvedProperties = new HashMap<>();
		for (int i = 0; i < properties.size(); i += 2) {
			resolvedProperties.put(properties.get(i), propertyResolver.apply(properties.get(i + 1)));
		}
		return resolvedProperties;
	}
}
