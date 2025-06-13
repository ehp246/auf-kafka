package me.ehp246.aufkafka.api.producer;

import java.time.Instant;
import java.util.List;

import org.apache.kafka.clients.producer.ProducerRecord;

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

    static OutboundEvent withValue(final String topic, Object value) {
        return new OutboundEvent() {

            @Override
            public String topic() {
                return topic;
            }

            @Override
            public Object value() {
                return value;
            }

        };
    }
}
