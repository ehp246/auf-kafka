package me.ehp246.aufkafka.core.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.ehp246.aufkafka.api.common.AufKafkaConstant;
import me.ehp246.aufkafka.api.consumer.DispatchListener;
import me.ehp246.aufkafka.api.consumer.InboundEvent;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class IgnoringConsumerExceptionListener implements DispatchListener.ExceptionListener {
    private final static Logger LOGGER = LoggerFactory.getLogger(IgnoringConsumerExceptionListener.class);

    @Override
    public void onException(final InboundEvent event, final Exception thrown) {
	LOGGER.atError().setCause(thrown).addMarker(AufKafkaConstant.EXCEPTION)
		.setMessage("Failed to consume: {}, {}, {} because of {}").addArgument(() -> event.topic())
		.addArgument(() -> event.key()).addArgument(() -> event.offset()).addArgument(() -> thrown.getMessage())
		.log();

	LOGGER.atTrace().setCause(thrown).setMessage("{}").addMarker(AufKafkaConstant.EXCEPTION)
		.addMarker(AufKafkaConstant.VALUE).addArgument(() -> event.value()).log();
    }

}
