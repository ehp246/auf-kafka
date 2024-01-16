package me.ehp246.aufkafka.core.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.ehp246.aufkafka.api.consumer.InboundListener;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class IgnoringConsumerExceptionListener
        implements InboundListener.ExceptionListener {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(IgnoringConsumerExceptionListener.class);

    @Override
    public void onException(final Context context) {
        LOGGER.atError().setCause(context.thrown())
                .setMessage("Failed to consume: {}, {}, {} because of {}")
                .addArgument(() -> context.message().topic())
                .addArgument(() -> context.message().key())
                .addArgument(() -> context.message().offset())
                .addArgument(() -> context.thrown().getMessage()).log();
    }

}
