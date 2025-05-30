package me.ehp246.aufkafka.core.producer;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;

import me.ehp246.aufkafka.api.common.AufKafkaConstant;
import me.ehp246.aufkafka.api.producer.OutboundEvent;
import me.ehp246.aufkafka.api.producer.ProducerRecordBuilder;
import me.ehp246.aufkafka.api.serializer.JacksonObjectOf;
import me.ehp246.aufkafka.api.serializer.json.ToJson;

/**
 * @author Lei Yang
 *
 */
public final class DefaultProducerRecordBuilder implements ProducerRecordBuilder {
    private final ToJson toJson;
    private final String correlIdHeader;

    public DefaultProducerRecordBuilder(final ToJson toJson,
            @Value("${" + AufKafkaConstant.PROPERTY_HEADER_CORRELATIONID + ":" + AufKafkaConstant.CORRELATIONID_HEADER
                    + "}") final String correlIdHeader) {
        super();
        this.toJson = toJson;
        this.correlIdHeader = correlIdHeader;
    }

    @Override
    public ProducerRecord<String, String> apply(OutboundEvent outboundEvent) {
        return new ProducerRecord<String, String>(outboundEvent.topic(), outboundEvent.partition(),
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
