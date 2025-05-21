package me.ehp246.aufkafka.core.consumer;

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

import me.ehp246.aufkafka.api.consumer.InboundEvent;

/**
 * A convenient wrapper of {@linkplain ConsumerRecord}.
 * 
 * @author Lei Yang
 */
public final class InboundRecord implements InboundEvent {
    private final ConsumerRecord<String, String> consumerRecord;
    private final Map<String, List<String>> headerMap;

    public InboundRecord(ConsumerRecord<String, String> consumerRecord) {
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

    @Override
    public ConsumerRecord<String, String> consumerRecord() {
        return this.consumerRecord;
    }

    @Override
    public String key() {
        return this.consumerRecord.key();
    }

    @Override
    public String topic() {
        return this.consumerRecord.topic();
    }

    @Override
    public String value() {
        return this.consumerRecord.value();
    }

    /**
     * Returns {@linkplain Map} and {@linkplain List} that are not modifiable.
     */
    @Override
    public Map<String, List<String>> headerMap() {
        return this.headerMap;
    }

    /**
     * Returns an {@linkplain Optional} containing the the last value if the key
     * exists. Note the value could be <code>null</code> for an existing key.
     * <p>
     * If the key does not exist, the {@linkplain Optional} will empty.
     */
    @Override
    public Optional<String> lastHeader(final String key) {
        final var values = this.headerMap.get(key);
        return values == null ? Optional.empty() : Optional.ofNullable(values.getLast());
    }

    @Override
    public <T> T lastHeader(final String key, final Function<String, T> parser) {
        final var value = this.lastHeader(key).orElse(null);
        return value == null ? null : parser.apply(value);
    }

    @Override
    public List<String> headerValues(final String key) {
        return this.headerMap.get(key);
    }

    @Override
    public Long timestamp() {
        return this.consumerRecord.timestamp();
    }

    @Override
    public int partition() {
        return this.consumerRecord.partition();
    }

    @Override
    public long offset() {
        return this.consumerRecord.offset();
    }

    @Override
    public Headers headers() {
        return this.consumerRecord.headers();
    }

    @Override
    public List<Header> headerList() {
        return StreamSupport.stream(this.consumerRecord.headers().spliterator(), false).toList();
    }
}
