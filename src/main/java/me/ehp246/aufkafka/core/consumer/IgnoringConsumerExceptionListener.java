package me.ehp246.aufkafka.core.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class IgnoringConsumerExceptionListener implements ConsumptionExceptionListener {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(IgnoringConsumerExceptionListener.class);

    @Override
    public void apply(final Context context) {
        LOGGER.atError().setCause(context.thrown())
                .setMessage("Failed to consume: {}, {}, {} because of {}")
                .addArgument(() -> context.received().topic())
                .addArgument(() -> context.received().key())
                .addArgument(() -> context.received().offset())
                .addArgument(() -> context.thrown().getMessage()).log();
    }

}
