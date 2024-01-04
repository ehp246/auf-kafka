package me.ehp246.aufkafka.api.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class NoopConsumer implements MsgConsumer {
    private final static Logger LOGGER = LoggerFactory.getLogger(NoopConsumer.class);

    @Override
    public void apply(final ConsumerRecord<String, String> msg) {
        LOGGER.atTrace().setMessage("Noop on: key '{}', topic '{}'").addArgument(msg::key)
                .addArgument(msg::topic).log();
    }
}
