package me.ehp246.aufkafka.core.producer;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import me.ehp246.aufkafka.api.producer.OutboundRecord;
import me.ehp246.aufkafka.api.producer.ProxyInvocationBinder;

/**
 * @author Lei Yang
 *
 */
record DefaultProxyInvocationBinder(Function<Object[], String> topicBinder,
        Function<Object[], String> keyBinder, Function<Object[], Object> partitionBinder,
        Function<Object[], Instant> timestampBinder, Function<Object[], String> correlIdBinder,
        int valueParamIndex, Map<Integer, HeaderParam> headerBinder,
        List<OutboundRecord.Header> headerStatic) implements ProxyInvocationBinder {
    @Override
    public Bound apply(final Object target, final Object[] args) throws Throwable {
        final var topic = topicBinder.apply(args);
        final var key = keyBinder.apply(args);
        final var partition = partitionBinder.apply(args);
        final var timestamp = timestampBinder.apply(args);
        final var value = valueParamIndex == -1 ? null : args[valueParamIndex];
        final var headers = Stream.concat(this.headerBinder.entrySet().stream()
                .filter(entry -> args[entry.getKey()] != null).map(entry -> {
                    final var argIndex = entry.getKey();
                    final var headerParam = entry.getValue();
                    final var name = headerParam.name();
                    final var type = headerParam.type();
                    final var arg = args[argIndex];

                    return new OutboundRecord.Header() {

                        @Override
                        public String name() {
                            return name;
                        }

                        @Override
                        public Object value() {
                            return arg;
                        }

                    };
                }), this.headerStatic.stream()).collect(Collectors.toList());

        return new Bound(new OutboundRecord() {

            @Override
            public String topic() {
                return topic;
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
