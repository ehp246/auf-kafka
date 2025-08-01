package me.ehp246.aufkafka.api.consumer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.record.TimestampType;

/**
 * A convenient wrapper of {@linkplain ConsumerRecord}.
 * 
 * @author Lei Yang
 */
public class InboundEvent {
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

    public String key() {
        return this.consumerRecord.key();
    }

    public String topic() {
        return this.consumerRecord.topic();
    }

    public String value() {
        return this.consumerRecord.value();
    }

    public Long timestamp() {
        return this.consumerRecord.timestamp();
    }

    public TimestampType timestampType() {
        return this.consumerRecord.timestampType();
    }

    public int partition() {
        return this.consumerRecord.partition();
    }

    public long offset() {
        return this.consumerRecord.offset();
    }

    public Headers headers() {
        return this.consumerRecord.headers();
    }

    /**
     * Returns {@linkplain Map} and {@linkplain List} that are not modifiable.
     */
    public Map<String, List<String>> headerMap() {
        return this.headerMap;
    }

    /**
     * Returns an {@linkplain Optional} containing the the last value if the key
     * exists. Note the value could be <code>null</code> for an existing key.
     * <p>
     * If the key does not exist, the {@linkplain Optional} will empty.
     */
    public Optional<String> lastHeader(final String key) {
        final var values = this.headerMap.get(key);
        return values == null ? Optional.empty() : Optional.ofNullable(values.getLast());
    }

    public <T> T lastHeader(final String key, final Function<String, T> parser) {
        final var value = this.lastHeader(key).orElse(null);
        return value == null ? null : parser.apply(value);
    }

    public List<String> headerValues(final String key) {
        return this.headerMap.get(key);
    }

    public List<Header> headerList() {
        return StreamSupport.stream(this.consumerRecord.headers().spliterator(), false).toList();
    }
}
