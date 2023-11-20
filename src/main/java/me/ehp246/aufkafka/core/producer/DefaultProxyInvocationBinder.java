package me.ehp246.aufkafka.core.producer;

import java.time.Instant;
import java.util.function.Function;

import me.ehp246.aufkafka.api.producer.OutboundRecord;
import me.ehp246.aufkafka.api.producer.ProxyInvocationBinder;

/**
 * @author Lei Yang
 *
 */
record DefaultProxyInvocationBinder(Function<Object[], String> topicBinder, Function<Object[], String> keyBinder,
        Function<Object[], Integer> partitionBinder, Function<Object[], Instant> timestampBinder,
        Function<Object[], String> correlIdBinder)
        implements ProxyInvocationBinder {
    @Override
    public Bound apply(final Object target, final Object[] args) throws Throwable {
        final var topic = topicBinder.apply(args);
        final var key = keyBinder.apply(args);
        final var partition = partitionBinder.apply(args);
        final var timestamp = timestampBinder.apply(args);

        return new Bound(new OutboundRecord() {

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
                // TODO Auto-generated method stub
                return OutboundRecord.super.value();
            }

            @Override
            public Instant timestamp() {
                return timestamp;
            }

        });
    }

}
