package me.ehp246.aufkafka.core.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class IgnoringConsumerExceptionListener implements ConsumerExceptionListener {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(IgnoringConsumerExceptionListener.class);

    @Override
    public void apply(final ConsumerExceptionContext context) {
        LOGGER.atError().setCause(context.exception())
                .setMessage("Failed to consume: {}, {}, {} because of {}")
                .addArgument(() -> context.msg().topic()).addArgument(() -> context.msg().key())
                .addArgument(() -> context.msg().offset())
                .addArgument(() -> context.exception().getMessage()).log();
    }

}
