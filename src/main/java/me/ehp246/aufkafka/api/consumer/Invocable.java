package me.ehp246.aufkafka.api.consumer;

import java.lang.reflect.Method;

import me.ehp246.aufkafka.api.annotation.Applying;
import me.ehp246.aufkafka.api.annotation.ForKey;

/**
 * @author Lei Yang
 * @since 1.0
 */
public interface Invocable extends AutoCloseable {
    Object instance();

    Method method();

    default InvocationModel invocationModel() {
        return InvocationModel.DEFAULT;
    }

    /**
     * The {@linkplain AutoCloseable} will be invoked by
     * {@linkplain InboundEndpoint} after the {@linkplain Applying} method returns
     * normally or aborts by throwing an exception.
     * <p>
     * The API is intended for best-effort clean-up purpose. Exception from
     * {@linkplain Invocable#close()} execution will be logged and suppressed so
     * that it does not impact further execution of the {@linkplain ForKey}.
     */
    @Override
    default void close() throws Exception {
    }
}
