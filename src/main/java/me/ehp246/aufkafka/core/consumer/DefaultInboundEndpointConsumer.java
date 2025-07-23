package me.ehp246.aufkafka.core.consumer;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.ehp246.aufkafka.api.consumer.DispatchListener;
import me.ehp246.aufkafka.api.consumer.EndpointAt;
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

    private final EndpointAt at;
    private final Consumer<String, String> consumer;
    private final Supplier<Duration> pollDurationSupplier;
    private final EventInvocableRunnableBuilder runnableBuilder;
    private final InvocableFactory invocableFactory;
    private final List<DispatchListener.DispatchingListener> onDispatching;
    private final DispatchListener.UnknownEventListener onUnknown;
    private final DispatchListener.ExceptionListener onException;

    private volatile CompletableFuture<Void> isDispatchingDone = CompletableFuture.completedFuture(null);
    private volatile boolean isClosed = false;
    private final CompletableFuture<Void> hasClosed = new CompletableFuture<>();
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    DefaultInboundEndpointConsumer(final EndpointAt at, final Consumer<String, String> consumer,
            final Supplier<Duration> pollDurationSupplier, final EventInvocableRunnableBuilder dispatcher,
            final InvocableFactory invocableFactory, final List<DispatchListener.DispatchingListener> onDispatching,
            final DispatchListener.UnknownEventListener onUnmatched,
            final DispatchListener.ExceptionListener onException) {
        super();
        this.at = at;
        this.consumer = consumer;
        this.pollDurationSupplier = pollDurationSupplier;
        this.runnableBuilder = dispatcher;
        this.invocableFactory = invocableFactory;
        this.onDispatching = onDispatching == null ? List.of() : onDispatching;
        this.onUnknown = onUnmatched;
        this.onException = onException;
    }

    @Override
    public void run() {
        final var topic = this.at.topic();
        final var partitions = this.at.partitions();

        if (partitions == null || partitions.size() == 0) {
            consumer.subscribe(Set.of(topic));
        } else {
            consumer.assign(partitions.stream().map(i -> new TopicPartition(topic, i)).toList());
        }

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

        this.isDispatchingDone = new CompletableFuture<>();
        this.consumer.pause(this.consumer.assignment());

        this.executor.execute(() -> {
            StreamSupport.stream(polled.spliterator(), false).map(InboundEvent::new).forEach(event -> {
                final var context = new InboundEventContext(event, this.consumer);
                try {
                    dispatchEvent(context);
                } catch (Exception e) {
                    if (this.onException != null) {
                        try {
                            this.onException.onException(context, e);
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

    private void dispatchEvent(final InboundEventContext context) throws Exception {
        try (final var closeable = EventMdcContext.set(context);) {
            this.onDispatching.stream().forEach(l -> l.onDispatching(context));

            final var invocable = invocableFactory.get(context.event());

            if (invocable == null) {
                if (onUnknown == null) {
                    throw new UnknownEventException(context.event());
                } else {
                    onUnknown.onUnknown(context);
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

        this.hasClosed.complete(null);
        this.isClosed = true;
    }

    @Override
    public CompletableFuture<Void> close() {
        this.isClosed = true;
        this.consumer.wakeup();
        return this.hasClosed;
    }
}
