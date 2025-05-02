package me.ehp246.aufkafka.core.producer;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import me.ehp246.aufkafka.api.producer.OutboundRecord;
import me.ehp246.aufkafka.api.producer.ProxyInvocationBinder;
import me.ehp246.aufkafka.api.serializer.ObjectOf;

/**
 * @author Lei Yang
 *
 */
record DefaultProxyInvocationBinder(Function<Object[], String> topicBinder, Function<Object[], String> eventTypeBinder,
        Function<Object[], String> keyBinder, Function<Object[], Object> partitionBinder,
        Function<Object[], Instant> timestampBinder, Function<Object[], String> correlIdBinder, ValueParam valueParam,
        Map<Integer, HeaderParam> headerBinder, List<OutboundRecord.Header> headerStatic)
        implements ProxyInvocationBinder {

    @Override
    public Bound apply(final Object target, final Object[] args) throws Throwable {
        final var topic = topicBinder.apply(args);
        final var eventType = eventTypeBinder.apply(args);
        final var key = keyBinder.apply(args);
        final var partition = partitionBinder.apply(args);
        final var timestamp = timestampBinder.apply(args);
        final var value = valueParam == null ? null : args[valueParam.index()];
        final var objectOf = valueParam == null ? null : valueParam.objectOf();
        final var headers = Stream
                .concat(this.headerBinder.entrySet().stream().map(entry -> new OutboundRecord.Header() {
                    final String key = entry.getValue().name();
                    final Object value = args[entry.getKey()];

                    @Override
                    public Object value() {
                        return value;
                    }

                    @Override
                    public String key() {
                        return key;
                    }
                }), this.headerStatic.stream()).collect(Collectors.toList());

        return new Bound(new OutboundRecord() {

            @Override
            public String topic() {
                return topic;
            }

            @Override
            public String eventType() {
                return eventType;
            }

            @Override
            public Object partitionKey() {
                return partition;
            }

            @Override
            public String key() {
                return key;
            }

            @Override
            public Object value() {
                return value;
            }

            @Override
            public ObjectOf<?> objectOf() {
                return objectOf;
            }

            @Override
            public Instant timestamp() {
                return timestamp;
            }

            @Override
            public Iterable<Header> headers() {
                return headers;
            }

        });
    }
}
