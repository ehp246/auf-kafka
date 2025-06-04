package me.ehp246.aufkafka.core.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.ehp246.aufkafka.api.common.AufKafkaConstant;
import me.ehp246.aufkafka.api.consumer.EventDispatchListener;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class IgnoringConsumerExceptionListener implements EventDispatchListener.ExceptionListener {
    private final static Logger LOGGER = LoggerFactory.getLogger(IgnoringConsumerExceptionListener.class);

    @Override
    public void onException(final Context context) {
	LOGGER.atError().setCause(context.thrown()).addMarker(AufKafkaConstant.EXCEPTION)
		.setMessage("Failed to consume: {}, {}, {} because of {}").addArgument(() -> context.event().topic())
		.addArgument(() -> context.event().key()).addArgument(() -> context.event().offset())
		.addArgument(() -> context.thrown().getMessage()).log();

	LOGGER.atTrace().setCause(context.thrown()).setMessage("{}").addMarker(AufKafkaConstant.EXCEPTION)
		.addMarker(AufKafkaConstant.VALUE).addArgument(() -> context.event().value()).log();
    }

}
