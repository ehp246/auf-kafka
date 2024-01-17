package me.ehp246.aufkafka.api.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.ehp246.aufkafka.api.AufKafkaConstant;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class NoOpUnmatchedConsumer implements UnmatchedConsumer {
    private final static Logger LOGGER = LoggerFactory.getLogger(NoOpUnmatchedConsumer.class);

    @Override
    public void accept(final ConsumerRecord<String, String> msg) {
        LOGGER.atInfo().setMessage("No op on: key '{}', topic '{}', offset '{}'")
                .addArgument(msg::key).addArgument(msg::topic).addArgument(msg::offset).log();

        LOGGER.atTrace().addMarker(AufKafkaConstant.VALUE).setMessage("{}").addArgument(msg::value)
                .log();
    }
}
