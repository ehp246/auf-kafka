package me.ehp246.aufkafka.core.consumer;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.ehp246.aufkafka.api.common.AufKafkaConstant;
import me.ehp246.aufkafka.api.consumer.DispatchListener;
import me.ehp246.aufkafka.api.consumer.InboundEventContext;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class IgnoringConsumerExceptionListener implements DispatchListener.ExceptionListener {
    private final static Logger LOGGER = LoggerFactory.getLogger(IgnoringConsumerExceptionListener.class);

    @Override
    public void onException(final InboundEventContext context, final Exception thrown) {
        final var event = context.event();

        LOGGER.atError().setCause(thrown).addMarker(AufKafkaConstant.EXCEPTION)
                .setMessage("Failed to consume: {}, {}, {} because of {}").addArgument((Supplier<Object>) event::topic)
                .addArgument((Supplier<Object>) event::key).addArgument((Supplier<Object>) event::offset)
                .addArgument((Supplier<Object>) thrown::getMessage).log();

        LOGGER.atTrace().setCause(thrown).setMessage("{}").addMarker(AufKafkaConstant.EXCEPTION)
                .addMarker(AufKafkaConstant.VALUE).addArgument((Supplier<Object>) event::value).log();
    }

}
