package me.ehp246.test.mock;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.Uuid;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.record.TimestampType;

/**
 * @author Lei Yang
 *
 */
public class MockConsumerRecord extends ConsumerRecord<String, String> {
    public MockConsumerRecord(String topic, int partition, long offset, long timestamp,
            TimestampType timestampType, int serializedKeySize, int serializedValueSize, String key,
            String value, Headers headers, Optional<Integer> leaderEpoch) {
        super(topic, partition, offset, timestamp, timestampType, serializedKeySize,
                serializedValueSize, key, value, headers, leaderEpoch);
    }

    public MockConsumerRecord(final String value, String... headers) {
        this(UUID.randomUUID().toString(), 0, 0, Instant.now().toEpochMilli(),
                TimestampType.CREATE_TIME, 0, value == null ? 0 : value.length() * 2, null, value,
                StringHeader.headers(headers), Optional.ofNullable(null));
    }

    public MockConsumerRecord(String topic, int partition, long offset, String key, String value) {
        super(topic, partition, offset, key, value);
    }

    public MockConsumerRecord() {
        this(Uuid.randomUuid().toString(), 0, 0, null, null);
    }

    public static MockConsumerRecord withHeaders(final Headers headers) {
        return new MockConsumerRecord(UUID.randomUUID().toString(), 0, 0,
                Instant.now().toEpochMilli(), TimestampType.CREATE_TIME, 0, 0, null, null, headers,
                Optional.ofNullable(null));
    }

    public static MockConsumerRecord withHeaders(final String... headers) {
        return withHeaders(StringHeader.headers(headers));
    }

    public static MockConsumerRecord withValue(final String value) {
        return new MockConsumerRecord(UUID.randomUUID().toString(), 0, 0,
                UUID.randomUUID().toString(), value);
    }
}
