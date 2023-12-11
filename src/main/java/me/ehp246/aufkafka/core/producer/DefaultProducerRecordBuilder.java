package me.ehp246.aufkafka.core.producer;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.header.Header;

import me.ehp246.aufkafka.api.producer.OutboundRecord;
import me.ehp246.aufkafka.api.producer.PartitionMap;
import me.ehp246.aufkafka.api.producer.ProducerRecordBuilder;

/**
 * @author Lei Yang
 *
 */
public final class DefaultProducerRecordBuilder implements ProducerRecordBuilder {
    private final PartitionMap partitionMap;
    private final Function<String, List<PartitionInfo>> infoProvider;

    public DefaultProducerRecordBuilder(
            final Function<String, List<PartitionInfo>> partitionInfoProvider,
            PartitionMap partitionMap) {
        super();
        this.partitionMap = partitionMap;
        this.infoProvider = partitionInfoProvider;
    }

    @Override
    public ProducerRecord<String, String> apply(OutboundRecord outboundRecord) {
        return new ProducerRecord<String, String>(outboundRecord.topic(),
                partitionMap.apply(this.infoProvider.apply(outboundRecord.topic()),
                        outboundRecord.partitionKey()),
                Optional.ofNullable(outboundRecord.timestamp()).map(Instant::toEpochMilli)
                        .orElse(null),
                outboundRecord.key(), null, headers(outboundRecord));
    }

    private Iterable<Header> headers(final OutboundRecord outboundRecord) {
        final var pairs = outboundRecord.headers();
        if (pairs == null) {
            return null;
        }

        return StreamSupport.stream(pairs.spliterator(), false).map(pair -> new Header() {
            private final String key = pair.key();
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
        }).collect(Collectors.toList());
    }
}
