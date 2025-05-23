package me.ehp246.aufkafka.api.producer;

import java.time.Instant;

import org.apache.kafka.clients.producer.ProducerRecord;

import me.ehp246.aufkafka.api.serializer.ObjectOf;

/**
 * The customized abstraction of a {@linkplain ProducerRecord}.
 * 
 * @author Lei Yang
 * @since 1.0
 */
public interface OutboundEvent {
    String topic();

    default String key() {
        return null;
    }

    default Object partitionKey() {
        return null;
    }

    default Object value() {
        return null;
    }

    default ObjectOf<?> objectOf() {
        return null;
    }

    default Instant timestamp() {
        return null;
    }

    default Iterable<Header> headers() {
        return null;
    }

    interface Header {
        String key();

        Object value();
    }
}
