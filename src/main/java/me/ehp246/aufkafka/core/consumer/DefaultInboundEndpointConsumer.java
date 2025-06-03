package me.ehp246.aufkafka.core.consumer;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.ehp246.aufkafka.api.consumer.ConsumerExceptionListener;
import me.ehp246.aufkafka.api.consumer.EventInvocableRunnableBuilder;
import me.ehp246.aufkafka.api.consumer.InboundConsumerListener;
import me.ehp246.aufkafka.api.consumer.InboundEndpointConsumer;
import me.ehp246.aufkafka.api.consumer.InboundEvent;
import me.ehp246.aufkafka.api.consumer.InvocableFactory;
import me.ehp246.aufkafka.api.consumer.UnmatchedConsumer;
import me.ehp246.aufkafka.api.exception.UnknownEventException;
import me.ehp246.aufkafka.api.spi.EventMdcContext;

/**
 * @author Lei Yang
 * @since 1.0
 */
final class DefaultInboundEndpointConsumer implements InboundEndpointConsumer {
    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultInboundEndpointConsumer.class);

    private final Consumer<String, String> consumer;
    private final Supplier<Duration> pollDurationSupplier;
    private final EventInvocableRunnableBuilder runnableBuilder;
    private final InvocableFactory invocableFactory;
    private final List<InboundConsumerListener.DispatchingListener> onDispatching;
    private final UnmatchedConsumer onUnmatched;
    private final ConsumerExceptionListener onException;
    private volatile boolean closed = false;
    private final CompletableFuture<Boolean> closedFuture = new CompletableFuture<Boolean>();

    DefaultInboundEndpointConsumer(final Consumer<String, String> consumer,
	    final Supplier<Duration> pollDurationSupplier, final EventInvocableRunnableBuilder dispatcher,
	    final InvocableFactory invocableFactory,
	    final List<InboundConsumerListener.DispatchingListener> onDispatching, final UnmatchedConsumer onUnmatched,
	    final ConsumerExceptionListener onException) {
	super();
	this.consumer = consumer;
	this.pollDurationSupplier = pollDurationSupplier;
	this.runnableBuilder = dispatcher;
	this.invocableFactory = invocableFactory;
	this.onDispatching = onDispatching == null ? List.of() : onDispatching;
	this.onUnmatched = onUnmatched;
	this.onException = onException;
    }

    public void run() {
	while (!this.closed) {
	    try {
		poll();
	    } catch (WakeupException e) {
		if (this.closed) {
		    LOGGER.atTrace().setCause(e).setMessage("Woke up to close. Ignored").log();
		} else {
		    LOGGER.atError().setCause(e).setMessage("Wrongly woke up").log();
		}
	    }
	}

	this.consumer.close();
	this.closedFuture.complete(true);
    }

    private void poll() {
	final var polled = consumer.poll(pollDurationSupplier.get());
	if (polled.count() > 1) {
	    LOGGER.atWarn().setMessage("Polled count: {}").addArgument(polled::count).log();
	}

	StreamSupport.stream(polled.spliterator(), false).map(InboundEvent::new).forEach(this::dispatchEvent);

	if (polled.count() > 0) {
	    consumer.commitSync();
	}
    }

    private void dispatchEvent(final InboundEvent event) {
	try (final var closeble = EventMdcContext.set(event);) {
	    this.onDispatching.stream().forEach(l -> l.onDispatching(event));

	    final var invocable = invocableFactory.get(event);

	    if (invocable == null) {
		if (onUnmatched == null) {
		    throw new UnknownEventException(event);
		} else {
		    onUnmatched.accept(event);
		}
	    } else {
		runnableBuilder.apply(invocable, event).run();
	    }
	} catch (Exception e) {
	    LOGGER.atError().setCause(e)
		    .setMessage(
			    this.onException.getClass().getSimpleName() + " failed, ignored: {}, {}, {} because of {}")
		    .addArgument(event::topic).addArgument(event::key).addArgument(event::offset)
		    .addArgument(e::getMessage).log();

	    if (this.onException != null) {
		this.onException.onException(new ConsumerExceptionListener.Context(consumer, event, e));
	    }
	}
    }

    @Override
    public CompletableFuture<Boolean> close() {
	this.closed = true;
	this.consumer.wakeup();
	return this.closedFuture;
    }
}
