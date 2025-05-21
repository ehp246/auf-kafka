package me.ehp246.aufkafka.api.consumer;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

public interface InboundEvent {

    ConsumerRecord<String, String> consumerRecord();

    String key();

    String topic();

    String value();

    /**
     * Returns {@linkplain Map} and {@linkplain List} that are not modifiable.
     */
    Map<String, List<String>> headerMap();

    /**
     * Returns an {@linkplain Optional} containing the the last value if the key
     * exists. Note the value could be <code>null</code> for an existing key.
     * <p>
     * If the key does not exist, the {@linkplain Optional} will empty.
     */
    Optional<String> lastHeader(String key);

    <T> T lastHeader(String key, Function<String, T> parser);

    List<String> headerValues(String key);

    Long timestamp();

    int partition();

    long offset();

    Headers headers();

    List<Header> headerList();

}