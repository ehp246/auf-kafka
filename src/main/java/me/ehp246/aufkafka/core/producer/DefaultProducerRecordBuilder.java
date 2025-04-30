package me.ehp246.aufkafka.core.producer;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.header.Header;

import me.ehp246.aufkafka.api.AufKafkaConstant;
import me.ehp246.aufkafka.api.producer.OutboundRecord;
import me.ehp246.aufkafka.api.producer.PartitionMap;
import me.ehp246.aufkafka.api.producer.ProducerRecordBuilder;
import me.ehp246.aufkafka.api.serializer.JacksonObjectOf;
import me.ehp246.aufkafka.api.serializer.json.ToJson;

/**
 * @author Lei Yang
 *
 */
public final class DefaultProducerRecordBuilder implements ProducerRecordBuilder {
    private final PartitionMap partitionMap;
    private final Function<String, List<PartitionInfo>> infoProvider;
    private final ToJson toJson;

    public DefaultProducerRecordBuilder(final Function<String, List<PartitionInfo>> partitionInfoProvider,
            final PartitionMap partitionMap, final ToJson toJson) {
        super();
        this.partitionMap = partitionMap;
        this.infoProvider = partitionInfoProvider;
        this.toJson = toJson;
    }

    @Override
    public ProducerRecord<String, String> apply(OutboundRecord outboundRecord) {
        return new ProducerRecord<String, String>(outboundRecord.topic(),
                partitionMap.apply(this.infoProvider.apply(outboundRecord.topic()), outboundRecord.partitionKey()),
                Optional.ofNullable(outboundRecord.timestamp()).map(Instant::toEpochMilli).orElse(null),
                outboundRecord.key(),
                this.toJson.apply(outboundRecord.value(), (JacksonObjectOf<?>) outboundRecord.objectOf()),
                headers(outboundRecord));
    }

    private Iterable<Header> headers(final OutboundRecord outboundRecord) {
        final var headers = new ArrayList<Header>();
        /**
         * Populate application headers first.
         */
        final var pairs = outboundRecord.headers();
        if (pairs != null && pairs.iterator().hasNext()) {
            StreamSupport.stream(pairs.spliterator(), false).map(pair -> new Header() {
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
            }).forEach(headers::add);
        }

        /**
         * Event type with higher priority
         */
        final var eventType = outboundRecord.eventType();
        headers.add(new Header() {
            private final byte[] value = eventType == null ? null : eventType.getBytes(StandardCharsets.UTF_8);

            @Override
            public byte[] value() {
                return value;
            }

            @Override
            public String key() {
                return AufKafkaConstant.EVENT_TYPE_HEADER;
            }
        });

        return headers;
    }
}
