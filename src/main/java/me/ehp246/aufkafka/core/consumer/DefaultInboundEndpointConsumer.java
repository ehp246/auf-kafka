package me.ehp246.aufkafka.core.consumer;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.ehp246.aufkafka.api.consumer.DispatchListener;
import me.ehp246.aufkafka.api.consumer.InboundEndpointConsumer;
import me.ehp246.aufkafka.api.consumer.InboundEvent;
import me.ehp246.aufkafka.api.consumer.InboundEventContext;
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

    private volatile CompletableFuture<Object> isDispatchingDone = CompletableFuture.completedFuture(null);
    private volatile boolean isClosed = false;
    private final CompletableFuture<Boolean> hasClosed = new CompletableFuture<Boolean>();
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    DefaultInboundEndpointConsumer(final Consumer<String, String> consumer,
            final Supplier<Duration> pollDurationSupplier, final EventInvocableRunnableBuilder dispatcher,
            final InvocableFactory invocableFactory, final List<DispatchListener.DispatchingListener> onDispatching,
            final DispatchListener.UnknownEventListener onUnmatched,
            final DispatchListener.ExceptionListener onException) {
        super();
        this.consumer = consumer;
        this.pollDurationSupplier = pollDurationSupplier;
        this.runnableBuilder = dispatcher;
        this.invocableFactory = invocableFactory;
        this.onDispatching = onDispatching == null ? List.of() : onDispatching;
        this.onUnknown = onUnmatched;
        this.onException = onException;
    }

    public void run() {
        while (!this.isClosed) {
            try {
                synchronized (this.consumer) {
                    this.poll();
                }
            } catch (WakeupException e) {
                if (this.isClosed) {
                    /*
                     * Expected. Do nothing.
                     */
                } else {
                    throw e;
                }
            } catch (Exception e) {
                waitToClose();
                throw e;
            }
        }

        waitToClose();
    }

    private void poll() {
        final var polled = consumer.poll(pollDurationSupplier.get());

        if (polled.count() <= 0) {
            return;
        }

        this.isDispatchingDone = new CompletableFuture<Object>();
        this.consumer.pause(this.consumer.assignment());

        this.executor.execute(() -> {
            StreamSupport.stream(polled.spliterator(), false).map(InboundEvent::new).forEach(event -> {
                try {
                    dispatchEvent(event);
                } catch (Exception e) {
                    if (this.onException != null) {
                        try {
                            this.onException.onException(event, e);
                        } catch (Exception e1) {
                            LOGGER.atError().setCause(e)
                                    .setMessage(
                                            this.onException.getClass().getSimpleName() + " failed, exception ignored.")
                                    .log();
                        }
                    } else {
                        LOGGER.atError().setCause(e)
                                .setMessage("Event dispatching failed on {}, {}, {}, exception ignored.")
                                .addArgument(event::topic).addArgument(event::key).addArgument(event::offset).log();

                    }
                }
            });

            synchronized (this.consumer) {
                consumer.commitSync();
                if (!isClosed) {
                    consumer.resume(consumer.assignment());
                }
            }

            this.isDispatchingDone.complete(null);
        });
    }

    private void dispatchEvent(final InboundEvent event) throws Exception {
        final var context = new InboundEventContext(event, this.consumer);
        try (final var closeable = EventMdcContext.set(event);) {
            this.onDispatching.stream().forEach(l -> l.onDispatching(event));

            final var invocable = invocableFactory.get(event);

            if (invocable == null) {
                if (onUnknown == null) {
                    throw new UnknownEventException(event);
                } else {
                    onUnknown.onUnknown(event);
                }
            } else {
                runnableBuilder.apply(invocable, context).run();
            }
        }
    }

    private void waitToClose() {
        try {
            this.isDispatchingDone.get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.atError().setCause(e).setMessage("Waiting for dispatching failed. Exception ignored.").log();
        }

        synchronized (this.consumer) {
            this.consumer.close();
        }

        this.hasClosed.complete(true);
        this.isClosed = true;
    }

    @Override
    public CompletableFuture<Boolean> close() {
        this.isClosed = true;
        this.consumer.wakeup();
        return this.hasClosed;
    }
}
