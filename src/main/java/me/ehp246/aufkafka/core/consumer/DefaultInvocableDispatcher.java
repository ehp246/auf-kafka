package me.ehp246.aufkafka.core.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;

import me.ehp246.aufkafka.api.AufKafkaConstant;
import me.ehp246.aufkafka.api.consumer.BoundInvocable;
import me.ehp246.aufkafka.api.consumer.Invocable;
import me.ehp246.aufkafka.api.consumer.InvocableBinder;
import me.ehp246.aufkafka.api.consumer.InvocableDispatcher;
import me.ehp246.aufkafka.api.consumer.InvocationListener;
import me.ehp246.aufkafka.api.consumer.InvocationModel;
import me.ehp246.aufkafka.api.consumer.Invoked.Completed;
import me.ehp246.aufkafka.api.consumer.Invoked.Failed;
import me.ehp246.aufkafka.api.exception.BoundInvocationFailedException;
import me.ehp246.aufkafka.api.spi.MsgMDCContext;

/**
 * @author Lei Yang
 * @since 1.0
 */
final class DefaultInvocableDispatcher implements InvocableDispatcher {
    private final static Logger LOGGER = LoggerFactory.getLogger(InvocableDispatcher.class);

    private final Executor executor;
    private final InvocableBinder binder;
    private final List<InvocationListener.OnInvoking> invoking = new ArrayList<>();
    private final List<InvocationListener.OnCompleted> completed = new ArrayList<>();
    private final List<InvocationListener.OnFailed> failed = new ArrayList<>();

    public DefaultInvocableDispatcher(final InvocableBinder binder,
            @Nullable final List<InvocationListener> listeners, @Nullable final Executor executor) {
        super();
        this.binder = binder;
        this.executor = executor;
        for (final var listener : listeners == null ? List.of() : listeners) {
            // null tolerating
            if (listener instanceof final InvocationListener.OnInvoking invoking) {
                this.invoking.add(invoking);
            }
            if (listener instanceof final InvocationListener.OnCompleted completed) {
                this.completed.add(completed);
            }
            if (listener instanceof final InvocationListener.OnFailed failed) {
                this.failed.add(failed);
            }
        }
    }

    @Override
    public void dispatch(final Invocable invocable, final ConsumerRecord<String, String> msg) {
        /*
         * The runnable returned is expected to handle all execution and exception. The
         * caller simply invokes this runnable without further processing.
         */
        final var boundRef = new BoundInvocable[] { null };
        final var runnable = (Runnable) () -> {
            try {
                boundRef[0] = binder.bind(invocable, msg);

                final var bound = boundRef[0];

                Optional.ofNullable(bound.mdcMap()).map(Map::entrySet)
                        .filter(set -> !set.isEmpty()).ifPresent(set -> set.stream()
                                .forEach(entry -> MDC.put(entry.getKey(), entry.getValue())));

                DefaultInvocableDispatcher.this.invoking
                        .forEach(listener -> listener.onInvoking(bound));

                final var outcome = bound.invoke();

                if (outcome instanceof final Failed failed) {
                    for (final var listener : DefaultInvocableDispatcher.this.failed) {
                        try {
                            listener.onFailed(failed);
                        } catch (final Exception e) {
                            failed.thrown().addSuppressed(e);
                        }
                    }

                    if (failed.thrown() instanceof RuntimeException re) {
                        throw re;
                    } else {
                        throw new BoundInvocationFailedException(failed.thrown());
                    }
                }

                final var completed = (Completed) outcome;
                DefaultInvocableDispatcher.this.completed
                        .forEach(listener -> listener.onCompleted(completed));
            } finally {
                try (invocable) {
                } catch (final Exception e) {
                    LOGGER.atWarn().setCause(e).addMarker(AufKafkaConstant.EXCEPTION)
                            .setMessage("Ignored: {}").addArgument(e::getMessage).log();
                }
                Optional.ofNullable(boundRef[0]).map(BoundInvocable::mdcMap).map(Map::keySet)
                        .map(Set::stream).ifPresent(s -> s.forEach(MDC::remove));
            }
        };

        if (executor == null || invocable.invocationModel() == InvocationModel.INLINE) {

            runnable.run();

        } else {
            executor.execute(() -> {
                try (final var closeable = MsgMDCContext.set(msg)) {
                    runnable.run();
                } catch (final Exception e) {
                    LOGGER.atWarn().setCause(e).addMarker(AufKafkaConstant.EXCEPTION)
                            .setMessage("Ignored: {}").addArgument(e::getMessage).log();
                }
            });
        }
    };
}
