package me.ehp246.aufkafka.core.consumer;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import org.apache.kafka.clients.consumer.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.ehp246.aufkafka.api.consumer.ConsumerExceptionListener;
import me.ehp246.aufkafka.api.consumer.EventInvocableDispatcher;
import me.ehp246.aufkafka.api.consumer.InboundConsumerListener;
import me.ehp246.aufkafka.api.consumer.InboundEndpointConsumer;
import me.ehp246.aufkafka.api.consumer.InvocableFactory;
import me.ehp246.aufkafka.api.consumer.UnmatchedConsumer;
import me.ehp246.aufkafka.api.exception.UnknownEventException;
import me.ehp246.aufkafka.api.spi.EventMDCContext;

/**
 * @author Lei Yang
 * @since 1.0
 */
final class InboundConsumerRunner implements Runnable, InboundEndpointConsumer {
	private final static Logger LOGGER = LoggerFactory.getLogger(InboundConsumerRunner.class);

	private final Consumer<String, String> consumer;
	private final Supplier<Duration> pollDurationSupplier;
	private final EventInvocableDispatcher dispatcher;
	private final InvocableFactory invocableFactory;
	private final List<InboundConsumerListener.DispatchingListener> onDispatching;
	private final UnmatchedConsumer onUnmatched;
	private final ConsumerExceptionListener onException;
	private volatile boolean closed = false;
	private final CompletableFuture<Boolean> closedFuture = new CompletableFuture<Boolean>();

	InboundConsumerRunner(final Consumer<String, String> consumer, final Supplier<Duration> pollDurationSupplier,
			final EventInvocableDispatcher dispatcher, final InvocableFactory invocableFactory,
			final List<InboundConsumerListener.DispatchingListener> onDispatching, final UnmatchedConsumer onUnmatched,
			final ConsumerExceptionListener onException) {
		super();
		this.consumer = consumer;
		this.pollDurationSupplier = pollDurationSupplier;
		this.dispatcher = dispatcher;
		this.invocableFactory = invocableFactory;
		this.onDispatching = onDispatching == null ? List.of() : onDispatching;
		this.onUnmatched = onUnmatched;
		this.onException = onException;
	}

	@Override
	public void run() {
		while (!this.closed) {
			final var polled = consumer.poll(pollDurationSupplier.get());
			if (polled.count() > 1) {
				LOGGER.atWarn().setMessage("Polled count: {}").addArgument(polled::count).log();
			}

			StreamSupport.stream(polled.spliterator(), false).map(InboundRecord::new).forEach(event -> {
				try (final var closeble = EventMDCContext.set(event);) {
					this.onDispatching.stream().forEach(l -> l.onDispatching(event));

					final var invocable = invocableFactory.get(event);

					if (invocable == null) {
						if (onUnmatched == null) {
							throw new UnknownEventException(event);
						} else {
							onUnmatched.accept(event);
						}
					} else {
						dispatcher.dispatch(invocable, event);
					}
				} catch (Exception e) {
					LOGGER.atError().setCause(e)
							.setMessage(this.onException.getClass().getSimpleName()
									+ " failed, ignored: {}, {}, {} because of {}")
							.addArgument(event::topic).addArgument(event::key).addArgument(event::offset)
							.addArgument(e::getMessage).log();

					if (this.onException != null) {
						this.onException.onException(new ConsumerExceptionListener.Context(consumer, event, e));
					}
				}
			});

			if (polled.count() > 0) {
				consumer.commitSync();
			}
		}

		this.consumer.close();
		this.closedFuture.complete(true);
	}

	@Override
	public Consumer<String, String> consumer() {
		return this.consumer;
	}

	public CompletableFuture<Boolean> close() {
		this.closed = true;
		return this.closedFuture;
	}
}
