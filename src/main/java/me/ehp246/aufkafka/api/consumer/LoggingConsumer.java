package me.ehp246.aufkafka.api.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.ehp246.aufkafka.api.AufKafkaConstant;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class LoggingConsumer implements InboundConsumerListener.DispatchingListener {
    private final static Logger LOGGER = LoggerFactory.getLogger(LoggingConsumer.class);

    @Override
    public void onDispatching(final ConsumerRecord<String, String> msg) {
        LOGGER.atDebug().setMessage("{}:{}, {}, {}").addArgument(msg::topic)
                .addArgument(msg::partition).addArgument(msg::key).addArgument(msg::offset).log();

        LOGGER.atTrace().addMarker(AufKafkaConstant.VALUE).setMessage("{}").addArgument(msg::value)
                .log();
    }

}
