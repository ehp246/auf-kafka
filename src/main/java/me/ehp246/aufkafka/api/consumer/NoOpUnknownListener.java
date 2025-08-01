package me.ehp246.aufkafka.api.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.ehp246.aufkafka.api.common.AufKafkaConstant;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class NoOpUnknownListener implements DispatchListener.UnknownEventListener {
    private final static Logger LOGGER = LoggerFactory.getLogger(NoOpUnknownListener.class);

    @Override
    public void onUnknown(final InboundEventContext context) {
        final var event = context.event();

        LOGGER.atInfo().setMessage("No op on: key '{}', topic '{}', offset '{}'").addArgument(event::key)
                .addArgument(event::topic).addArgument(event::offset).log();

        LOGGER.atTrace().addMarker(AufKafkaConstant.VALUE).setMessage("{}").addArgument(event::value).log();
    }
}
