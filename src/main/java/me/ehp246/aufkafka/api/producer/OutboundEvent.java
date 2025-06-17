package me.ehp246.aufkafka.api.producer;

import java.time.Instant;
import java.util.List;

import org.apache.kafka.clients.producer.ProducerRecord;

import me.ehp246.aufkafka.api.serializer.jackson.TypeOfJson;

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

    /**
     * Maps to {@linkplain ProducerRecord#partition()}. Could be <code>null</code>.
     */
    default Integer partition() {
        return null;
    }

    default Object value() {
        return null;
    }

    default TypeOfJson typeOf() {
        return this.value() == null ? null : TypeOfJson.of(this.value().getClass());
    }

    default Instant timestamp() {
        return null;
    }

    default List<Header> headers() {
        return null;
    }

    interface Header {
        String key();

        Object value();
    }
}
