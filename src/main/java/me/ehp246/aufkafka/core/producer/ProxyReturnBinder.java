package me.ehp246.aufkafka.core.producer;

import me.ehp246.aufkafka.api.producer.OutboundEvent;
import me.ehp246.aufkafka.api.producer.ProducerFn.ProducerFnRecord;

/**
 * @author Lei Yang
 *
 */
sealed interface ProxyReturnBinder {
}

@FunctionalInterface
non-sealed interface LocalReturnBinder extends ProxyReturnBinder {
    Object apply(OutboundEvent event, ProducerFnRecord sendRecord);
}
