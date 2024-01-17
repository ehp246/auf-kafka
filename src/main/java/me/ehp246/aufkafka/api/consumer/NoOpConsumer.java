package me.ehp246.aufkafka.api.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class NoOpConsumer implements UnmatchedConsumer {
    private final static Logger LOGGER = LoggerFactory.getLogger(NoOpConsumer.class);

    @Override
    public void accept(final ConsumerRecord<String, String> msg) {
        LOGGER.atTrace().setMessage("No op on: key '{}', topic '{}'").addArgument(msg::key)
                .addArgument(msg::topic).log();
    }
}
