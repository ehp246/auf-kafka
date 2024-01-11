package me.ehp246.aufkafka.core.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.ehp246.aufkafka.api.consumer.InboundEndpoint;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class IgnoringConsumerExceptionListener
        implements InboundEndpoint.EventListener.ExceptionListener {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(IgnoringConsumerExceptionListener.class);

    @Override
    public void onException(final ExceptionContext context) {
        LOGGER.atError().setCause(context.thrown())
                .setMessage("Failed to consume: {}, {}, {} because of {}")
                .addArgument(() -> context.received().topic())
                .addArgument(() -> context.received().key())
                .addArgument(() -> context.received().offset())
                .addArgument(() -> context.thrown().getMessage()).log();
    }

}
