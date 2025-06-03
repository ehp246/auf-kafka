package me.ehp246.aufkafka.core.producer;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import me.ehp246.aufkafka.api.producer.OutboundEvent;
import me.ehp246.aufkafka.api.producer.OutboundEvent.Header;
import me.ehp246.aufkafka.api.serializer.ObjectOf;

/**
 * @author Lei Yang
 *
 */
final class DefaultProxyInvocationBinder implements ProxyInvocationBinder {
    private final Function<Object[], String> topicBinder;
    private final Function<Object[], String> keyBinder;
    private final Function<Object[], Integer> partitionBinder;
    private final Function<Object[], Instant> timestampBinder;
    private final ValueParam valueParam;
    private final Map<Integer, HeaderParam> headerBinder;
    private final List<OutboundEvent.Header> headerStatic;

    DefaultProxyInvocationBinder(Function<Object[], String> topicBinder, Function<Object[], String> keyBinder,
            Function<Object[], Integer> partitionBinder, Function<Object[], Instant> timestampBinder,
            ValueParam valueParam, Map<Integer, HeaderParam> headerBinder, List<Header> headerStatic) {
        super();
        this.topicBinder = topicBinder;
        this.keyBinder = keyBinder;
        this.partitionBinder = partitionBinder;
        this.timestampBinder = timestampBinder;
        this.valueParam = valueParam;
        this.headerBinder = headerBinder;
        this.headerStatic = headerStatic;
    }

    @Override
    public OutboundEvent apply(final Object target, final Object[] args) throws Throwable {
        final var topic = topicBinder.apply(args);
        final var key = keyBinder.apply(args);
        final var partition = partitionBinder.apply(args);
        final var timestamp = timestampBinder.apply(args);
        final var value = valueParam == null ? null : args[valueParam.index()];
        final var objectOf = valueParam == null ? null : valueParam.objectOf();
        final var headers = Stream
                .concat(this.headerStatic.stream(),
                        this.headerBinder.entrySet().stream()
                                .map(entry -> new OutboundHeader(entry.getValue().name(), args[entry.getKey()])))
                .collect(Collectors.toList());

        return new OutboundEvent() {

            @Override
            public String topic() {
                return topic;
            }

            @Override
            public Integer partition() {
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
            public List<Header> headers() {
                return headers;
            }
        };
    }
}
