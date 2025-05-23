package me.ehp246.aufkafka.core.producer;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;

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
    public ProducerRecord<String, String> apply(OutboundEvent outboundEvent) {
        return new ProducerRecord<String, String>(outboundEvent.topic(),
                partitionFn.apply(this.infoProvider.apply(outboundEvent.topic()), outboundEvent.partitionKey()),
                Optional.ofNullable(outboundEvent.timestamp()).map(Instant::toEpochMilli).orElse(null),
                outboundEvent.key(),
                this.toJson.apply(outboundEvent.value(), (JacksonObjectOf<?>) outboundEvent.objectOf()),
                headers(outboundEvent));
    }

    private Iterable<Header> headers(final OutboundEvent outboundEvent) {
        final var headers = new ArrayList<Header>();
        /**
         * Populate application headers first.
         */
        final var pairs = outboundEvent.headers();
        final var keySet = new HashSet<String>();
        if (pairs != null && !pairs.isEmpty()) {
            for (var pair : pairs) {
                final var key = pair.key();
                keySet.add(key);
                headers.add(new RecordHeader(key,
                        pair.value() == null ? null : pair.value().toString().getBytes(StandardCharsets.UTF_8)));
            }
        }

        /**
         * Reserved headers to the last position.
         */
        if (!keySet.contains(this.correlIdHeader)) {
            headers.add(new RecordHeader(this.correlIdHeader,
                    UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8)));
        }

        return headers;
    }
}
