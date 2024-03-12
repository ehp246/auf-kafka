package me.ehp246.aufkafka.core.consumer;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import me.ehp246.aufkafka.api.consumer.InboundConsumerExecutorProvider;
import me.ehp246.aufkafka.api.consumer.InboundConsumerListener;
import me.ehp246.aufkafka.api.consumer.InboundEndpoint;
import me.ehp246.aufkafka.api.consumer.InvocableBinder;
import me.ehp246.aufkafka.api.consumer.LoggingDispatchingListener;

/**
 * @author Lei Yang
 * @see InboundEndpointFactory
 */
public final class InboundEndpointConsumerConfigurer implements SmartInitializingSingleton, AutoCloseable {
	private final static Logger LOGGER = LoggerFactory.getLogger(InboundEndpointConsumerConfigurer.class);

	private final List<InboundEndpoint> endpoints;
	private final InboundConsumerExecutorProvider executorProvider;
	private final InvocableBinder binder;
	private final ConsumerProvider consumerProvider;
	private final List<InboundConsumerListener.DispatchingListener> onDispatching;
	private final AutowireCapableBeanFactory autowireCapableBeanFactory;
	private final DefaultInboundConsumerRegistry consumerRegistry;

	public InboundEndpointConsumerConfigurer(final List<InboundEndpoint> endpoints,
			final InboundConsumerExecutorProvider executorProvider, final ConsumerProvider consumerProvider,
			final InvocableBinder binder, final LoggingDispatchingListener loggingDispatchingListener,
			final AutowireCapableBeanFactory autowireCapableBeanFactory,
			final DefaultInboundConsumerRegistry consumerRegistry) {
		super();
		this.endpoints = endpoints;
		this.executorProvider = executorProvider;
		this.consumerProvider = consumerProvider;
		this.binder = binder;
		this.onDispatching = loggingDispatchingListener == null ? List.of() : List.of(loggingDispatchingListener);
		this.autowireCapableBeanFactory = autowireCapableBeanFactory;
		this.consumerRegistry = consumerRegistry;
	}

	@Override
	public void afterSingletonsInstantiated() {
		for (final var endpoint : this.endpoints) {
			LOGGER.atTrace().setMessage("Registering '{}' on '{}'").addArgument(endpoint::name)
					.addArgument(() -> endpoint.from().topic()).log();

			final var consumer = this.consumerProvider.get(endpoint.consumerConfigName(),
					endpoint.consumerProperties());
			consumer.subscribe(Set.of(endpoint.from().topic()));

			final var consumerRunner = new InboundConsumerRunner(consumer, new DefaultInvocableDispatcher(this.binder,
					endpoint.invocationListener() == null ? null : List.of(endpoint.invocationListener()), null),
					new AutowireCapableInvocableFactory(autowireCapableBeanFactory, endpoint.keyRegistry()),
					this.onDispatching, endpoint.unmatchedConsumer(), endpoint.consumerExceptionListener());

			this.consumerRegistry.put(endpoint.name(), consumerRunner);

			this.executorProvider.get().execute(consumerRunner);
		}
	}

	@Override
	public void close() throws Exception {
		this.consumerRegistry.getNames().forEach(name -> this.consumerRegistry.remove(name).consumer().close());
	}
}
