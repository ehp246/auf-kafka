package me.ehp246.aufkafka.api.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.ehp246.aufkafka.api.AufKafkaConstant;

/**
 * Built-in logger on dispatching {@linkplain ConsumerRecord}.
 * 
 * @author Lei Yang
 */
public final class InboundDispatchingLogger implements InboundConsumerListener.DispatchingListener {
    private final static Logger LOGGER = LoggerFactory.getLogger(InboundDispatchingLogger.class);

    private final boolean enabled;

    public InboundDispatchingLogger(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void onDispatching(final ConsumerRecord<String, String> msg) {
        if (!this.enabled) {
            return;
        }

        LOGGER.atInfo().setMessage("{}:{}, {}, {}").addArgument(msg::topic).addArgument(msg::partition)
                .addArgument(msg::key).addArgument(msg::offset).log();

        LOGGER.atTrace().addMarker(AufKafkaConstant.VALUE).setMessage("{}").addArgument(msg::value).log();
    }

}
