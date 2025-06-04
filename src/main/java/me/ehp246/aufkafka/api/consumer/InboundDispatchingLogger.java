package me.ehp246.aufkafka.api.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.ehp246.aufkafka.api.common.AufKafkaConstant;

/**
 * Built-in logger on dispatching {@linkplain ConsumerRecord}.
 * 
 * @author Lei Yang
 */
public final class InboundDispatchingLogger implements DispatchListener.DispatchingListener {
    private final static Logger LOGGER = LoggerFactory.getLogger(InboundDispatchingLogger.class);

    private final boolean enabled;

    public InboundDispatchingLogger(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void onDispatching(final InboundEvent event) {
        if (!this.enabled) {
            return;
        }

        LOGGER.atInfo().setMessage("{}:{}, {}, {}").addArgument(event::topic).addArgument(event::partition)
                .addArgument(event::key).addArgument(event::offset).log();

        LOGGER.atTrace().addMarker(AufKafkaConstant.VALUE).setMessage("{}").addArgument(event::value).log();
    }

}
