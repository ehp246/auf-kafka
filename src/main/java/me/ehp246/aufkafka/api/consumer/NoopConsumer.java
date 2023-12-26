package me.ehp246.aufkafka.api.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class NoopConsumer implements MsgConsumer {
    private final static Logger LOGGER = LogManager.getLogger();

    @Override
    public void apply(final ConsumerRecord<String, String> msg) {
        LOGGER.atTrace().log("Noop on: key '{}', topic '{}'", msg::key, msg::topic);
    }
}
