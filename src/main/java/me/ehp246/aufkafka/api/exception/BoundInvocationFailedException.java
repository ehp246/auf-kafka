package me.ehp246.aufkafka.api.exception;

import me.ehp246.aufkafka.api.consumer.BoundInvocable;

/**
 * Indicates the invocation on {@linkplain BoundInvocable} has failed wrapping
 * the cause.
 *
 * @author Lei Yang
 * @since 1.0
 */
public final class BoundInvocationFailedException extends RuntimeException {
    private static final long serialVersionUID = -2145053393177279673L;

    public BoundInvocationFailedException(final Throwable cause) {
        super(cause);
    }
}
