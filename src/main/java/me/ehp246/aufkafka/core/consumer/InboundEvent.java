package me.ehp246.aufkafka.core.consumer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;

/**
 * A convenient wrapper of {@linkplain ConsumerRecord}.
 * 
 * @author Lei Yang
 */
public final class InboundEvent {
    private final ConsumerRecord<String, String> consumerRecord;
    private final Map<String, List<String>> headerMap;

    public InboundEvent(ConsumerRecord<String, String> consumerRecord) {
        this.consumerRecord = Objects.requireNonNull(consumerRecord);
        this.headerMap = Collections
                .unmodifiableMap(StreamSupport.stream(this.consumerRecord.headers().spliterator(), false)
                        .collect(Collectors.toMap(Header::key, header -> {
                            final var l = new ArrayList<String>();
                            l.add(header.value() == null ? null : new String(header.value(), StandardCharsets.UTF_8));
                            return l;
                        }, (l, r) -> {
                            l.addAll(r);
                            return l;
                        })).entrySet().stream().collect(Collectors.toMap(Entry::getKey,
                                entry -> Collections.unmodifiableList(entry.getValue()))));
    }

    public ConsumerRecord<String, String> consumerRecord() {
        return this.consumerRecord;
    }

    /**
     * Returned {@linkplain Map} and {@linkplain List} are not modifiable.
     */
    public Map<String, List<String>> headerMap() {
        return this.headerMap;
    }
}
