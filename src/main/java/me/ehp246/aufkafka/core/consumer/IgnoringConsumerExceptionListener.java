package me.ehp246.aufkafka.core.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.ehp246.aufkafka.api.AufKafkaConstant;
import me.ehp246.aufkafka.api.consumer.ConsumerExceptionListener;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class IgnoringConsumerExceptionListener implements ConsumerExceptionListener {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(IgnoringConsumerExceptionListener.class);

    @Override
    public void onException(final Context context) {
        LOGGER.atError().setCause(context.thrown()).addMarker(AufKafkaConstant.EXCEPTION)
                .setMessage("Failed to consume: {}, {}, {} because of {}")
                .addArgument(() -> context.message().topic())
                .addArgument(() -> context.message().key())
                .addArgument(() -> context.message().offset())
                .addArgument(() -> context.thrown().getMessage()).log();

        LOGGER.atTrace().setCause(context.thrown()).setMessage("{}")
                .addMarker(AufKafkaConstant.EXCEPTION).addMarker(AufKafkaConstant.VALUE)
                .addArgument(() -> context.message().value()).log();
    }

}
