package me.ehp246.aufkafka.api.spi;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;

import me.ehp246.aufkafka.api.AufKafkaConstant;
import me.ehp246.aufkafka.core.util.OneUtil;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class MsgMDCContext {
    private MsgMDCContext() {
    }

    private enum InboundContextName {
        AufKafkaFrom, AufKafkaCorrelationId, AufKafkaKey, AufKafkaType, AufKafkaMsgMDC;
    }

    public static AutoCloseable set(final ConsumerRecord<String, String> msg) {
        if (msg == null) {
            return () -> {
            };
        }
        final AutoCloseable closeable = () -> MsgMDCContext.clear(msg);

        MDC.put(InboundContextName.AufKafkaFrom.name(), OneUtil.toString(msg.topic()));
        MDC.put(InboundContextName.AufKafkaKey.name(), msg.key());

        final var propertyNames = OneUtil.toList(msg.headers());
        if (propertyNames == null) {
            return closeable;
        }

        propertyNames.stream().filter(name -> name.key().startsWith(AufKafkaConstant.MSG_MDC_HEADER_PREFIX))
                .forEach(name -> MDC.put(name.key().replaceFirst(AufKafkaConstant.MSG_MDC_HEADER_PREFIX, ""),
                        new String(name.value(), StandardCharsets.UTF_8)));

        return closeable;
    }

    public static void clear(final ConsumerRecord<String, String> msg) {
        if (msg == null) {
            return;
        }

        for (final var value : InboundContextName.values()) {
            MDC.remove(value.name());
        }

        final var propertyNames = OneUtil.toList(msg.headers());
        if (propertyNames == null) {
            return;
        }
        propertyNames.stream().filter(name -> name.key().startsWith(AufKafkaConstant.MSG_MDC_HEADER_PREFIX))
                .forEach(name -> MDC.remove(name.key().replaceFirst(AufKafkaConstant.MSG_MDC_HEADER_PREFIX, "")));
    }
}
