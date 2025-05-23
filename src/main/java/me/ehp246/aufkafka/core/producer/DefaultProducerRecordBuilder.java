package me.ehp246.aufkafka.core.producer;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.header.Header;

import me.ehp246.aufkafka.api.producer.OutboundEvent;
import me.ehp246.aufkafka.api.producer.PartitionFn;
import me.ehp246.aufkafka.api.producer.ProducerRecordBuilder;
import me.ehp246.aufkafka.api.serializer.JacksonObjectOf;
import me.ehp246.aufkafka.api.serializer.json.ToJson;

/**
 * @author Lei Yang
 *
 */
final class DefaultProducerRecordBuilder implements ProducerRecordBuilder {
    private final PartitionFn partitionFn;
    private final Function<String, List<PartitionInfo>> infoProvider;
    private final ToJson toJson;
    private final String correlIdHeader;

    public DefaultProducerRecordBuilder(final Function<String, List<PartitionInfo>> partitionInfoProvider,
            final PartitionFn partitionFn, final ToJson toJson, final String correlIdHeader) {
        super();
        this.partitionFn = partitionFn;
        this.infoProvider = partitionInfoProvider;
        this.toJson = toJson;
        this.correlIdHeader = correlIdHeader;
    }

    @Override
    public ProducerRecord<String, String> apply(OutboundEvent outboundRecord) {
        return new ProducerRecord<String, String>(outboundRecord.topic(),
                partitionFn.apply(this.infoProvider.apply(outboundRecord.topic()), outboundRecord.partitionKey()),
                Optional.ofNullable(outboundRecord.timestamp()).map(Instant::toEpochMilli).orElse(null),
                outboundRecord.key(),
                this.toJson.apply(outboundRecord.value(), (JacksonObjectOf<?>) outboundRecord.objectOf()),
                headers(outboundRecord));
    }

    private Iterable<Header> headers(final OutboundEvent outboundEvent) {
        final var headers = new ArrayList<Header>();
        /**
         * Populate application headers first.
         */
        final var pairs = outboundEvent.headers();
        var hasCorrelId = false;
        if (pairs != null && !pairs.isEmpty()) {
            for (var pair : pairs) {
                final var key = pair.key();
                hasCorrelId = this.correlIdHeader.equals(key);
                headers.add(new Header() {
                    private final byte[] value = pair.value() == null ? null
                            : pair.value().toString().getBytes(StandardCharsets.UTF_8);

                    @Override
                    public String key() {
                        return key;
                    }

                    @Override
                    public byte[] value() {
                        return value;
                    }
                });
            }
        }

        /**
         * Reserved headers to the last position.
         */
        if (!hasCorrelId) {
            headers.add(new Header() {
                final byte[] value = UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8);

                @Override
                public byte[] value() {
                    return value;
                }

                @Override
                public String key() {
                    return correlIdHeader;
                }
            });
        }

        return headers;
    }
}
