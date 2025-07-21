package me.ehp246.aufkafka.core.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;

import me.ehp246.aufkafka.api.common.AufKafkaConstant;
import me.ehp246.aufkafka.api.consumer.BoundInvocable;
import me.ehp246.aufkafka.api.consumer.EventInvocable;
import me.ehp246.aufkafka.api.consumer.EventInvocableBinder;
import me.ehp246.aufkafka.api.consumer.InboundEventContext;
import me.ehp246.aufkafka.api.consumer.InvocationListener;
import me.ehp246.aufkafka.api.consumer.Invoked.Completed;
import me.ehp246.aufkafka.api.consumer.Invoked.Failed;
import me.ehp246.aufkafka.api.exception.BoundInvocationFailedException;

/**
 * Default implementation of {@linkplain EventInvocableRunnableBuilder}.
 * 
 * @author Lei Yang
 */
final class DefaultEventInvocableRunnableBuilder implements EventInvocableRunnableBuilder {
    private final static Logger LOGGER = LoggerFactory.getLogger(EventInvocableRunnableBuilder.class);

    private final EventInvocableBinder binder;
    private final List<InvocationListener.InvokingListener> invoking = new ArrayList<>();
    private final List<InvocationListener.CompletedListener> completed = new ArrayList<>();
    private final List<InvocationListener.FailedListener> failed = new ArrayList<>();

    public DefaultEventInvocableRunnableBuilder(final EventInvocableBinder binder,
            @Nullable final List<InvocationListener> listeners) {
        super();
        this.binder = binder;
        for (final var listener : listeners == null ? List.of() : listeners) {
            // null tolerating
            if (listener instanceof final InvocationListener.InvokingListener invoking) {
                this.invoking.add(invoking);
            }
            if (listener instanceof final InvocationListener.CompletedListener completed) {
                this.completed.add(completed);
            }
            if (listener instanceof final InvocationListener.FailedListener failed) {
                this.failed.add(failed);
            }
        }
    }

    /**
     * The runnable returned is expected to handle all execution and exception. The
     * caller simply invokes this runnable without further processing.
     */
    @Override
    public Runnable apply(final EventInvocable eventInvocable, final InboundEventContext event) {
        final var boundRef = new BoundInvocable[] { null };
        return () -> {
            try {
                boundRef[0] = binder.bind(eventInvocable, event);

                Optional.ofNullable(boundRef[0].mdcMap()).map(Map::entrySet).filter(set -> !set.isEmpty())
                        .ifPresent(set -> set.stream().forEach(entry -> MDC.put(entry.getKey(), entry.getValue())));

                DefaultEventInvocableRunnableBuilder.this.invoking
                        .forEach(listener -> listener.onInvoking(boundRef[0]));

                final var outcome = boundRef[0].invoke();

                if (outcome instanceof final Failed failed) {
                    for (final var listener : DefaultEventInvocableRunnableBuilder.this.failed) {
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
                DefaultEventInvocableRunnableBuilder.this.completed
                        .forEach(listener -> listener.onCompleted(completed));
            } finally {
                try (eventInvocable) {
                } catch (final Exception e) {
                    LOGGER.atWarn().setCause(e).addMarker(AufKafkaConstant.EXCEPTION).setMessage("Ignored: {}")
                            .addArgument(e::getMessage).log();
                }
                Optional.ofNullable(boundRef[0]).map(BoundInvocable::mdcMap).map(Map::keySet).map(Set::stream)
                        .ifPresent(s -> s.forEach(MDC::remove));
            }
        };
    };
}
