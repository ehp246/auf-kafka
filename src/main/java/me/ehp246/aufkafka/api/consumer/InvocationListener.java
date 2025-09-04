package me.ehp246.aufkafka.api.consumer;

import me.ehp246.aufkafka.api.consumer.Invoked.Completed;
import me.ehp246.aufkafka.api.consumer.Invoked.Failed;

/**
 * @author Lei Yang
 * @since 1.0
 */
public sealed interface InvocationListener {
    @FunctionalInterface
    public non-sealed interface InvokingListener extends InvocationListener {
        void onInvoking(BoundInvocable bound);
    }

    @FunctionalInterface
    public non-sealed interface CompletedListener extends InvocationListener {
        void onCompleted(BoundInvocable bound, Completed completed);
    }

    /**
     * When an invocation fails on a {@linkplain BoundInvocable}, the best effort
     * will be made to call all {@linkplain FailedListener} listeners in turn
     * passing in the failure.
     * <p>
     * If a {@linkplain FailedListener} throws an exception, the exception will not
     * be propagated. Instead it will be added to the
     * {@linkplain Throwable#getSuppressed()} of the invocation failure which will
     * be passed to the next {@linkplain FailedListener}.
     * <p>
     * After all {@linkplain FailedListener} have been executed, the original
     * invocation failure will be thrown with suppressed exceptions from the
     * listeners.
     */
    @FunctionalInterface
    public non-sealed interface FailedListener extends InvocationListener {
        void onFailed(BoundInvocable bound, Failed failed);
    }
}
