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

import me.ehp246.aufkafka.api.consumer.DispatchListener;
import me.ehp246.aufkafka.api.consumer.EventInvocableRunnableBuilder;
import me.ehp246.aufkafka.api.consumer.InboundEndpointConsumer;
import me.ehp246.aufkafka.api.consumer.InboundEvent;
import me.ehp246.aufkafka.api.consumer.InvocableFactory;
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
    private final List<DispatchListener.DispatchingListener> onDispatching;
    private final DispatchListener.UnknownEventListener onUnknown;
    private final DispatchListener.ExceptionListener onException;
    private final InboundEndpointConsumer.Listener.ExceptionListener consumerExcepitonListener;
    private volatile boolean closed = false;
    private final CompletableFuture<Boolean> closedFuture = new CompletableFuture<Boolean>();

    DefaultInboundEndpointConsumer(final Consumer<String, String> consumer,
	    final Supplier<Duration> pollDurationSupplier, final EventInvocableRunnableBuilder dispatcher,
	    final InvocableFactory invocableFactory, final List<DispatchListener.DispatchingListener> onDispatching,
	    final DispatchListener.UnknownEventListener onUnmatched,
	    final DispatchListener.ExceptionListener onException,
	    final InboundEndpointConsumer.Listener consumerListener) {
	super();
	this.consumer = consumer;
	this.pollDurationSupplier = pollDurationSupplier;
	this.runnableBuilder = dispatcher;
	this.invocableFactory = invocableFactory;
	this.onDispatching = onDispatching == null ? List.of() : onDispatching;
	this.onUnknown = onUnmatched;
	this.onException = onException;
	this.consumerExcepitonListener = consumerListener instanceof Listener.ExceptionListener exListener ? exListener
		: null;
    }

    public void run() {
	while (!this.closed) {
	    try {
		poll();
	    } catch (WakeupException e) {
		if (this.closed) {
		    LOGGER.atTrace().setMessage("Woke up to close.").log();
		} else {
		    throw e;
		}
	    } catch (Exception e) {
		try {
		    if (this.consumerExcepitonListener != null) {
			this.consumerExcepitonListener.onException(this, e);
		    }
		} catch (Exception x) {
		    LOGGER.atWarn().setCause(x).setMessage("Exception from {} ignored.")
			    .addArgument(this.consumerExcepitonListener).log();
		}
		throw e;
	    }
	}

	this.consumer.close();
	this.closedFuture.complete(true);
    }

    private void poll() {
	final var polled = consumer.poll(pollDurationSupplier.get());

	StreamSupport.stream(polled.spliterator(), false).map(InboundEvent::new).forEach(this::dispatchEvent);

	if (polled.count() > 0) {
	    consumer.commitSync();
	}
    }

    private void dispatchEvent(final InboundEvent event) {
	try (final var closeable = EventMdcContext.set(event);) {
	    this.onDispatching.stream().forEach(l -> l.onDispatching(event));

	    final var invocable = invocableFactory.get(event);

	    if (invocable == null) {
		if (onUnknown == null) {
		    throw new UnknownEventException(event);
		} else {
		    onUnknown.onKnown(event);
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
		this.onException.onException(event, e);
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
