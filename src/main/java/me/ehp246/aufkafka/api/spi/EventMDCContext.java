package me.ehp246.aufkafka.api.spi;

import java.nio.charset.StandardCharsets;

import org.slf4j.MDC;

import me.ehp246.aufkafka.api.common.AufKafkaConstant;
import me.ehp246.aufkafka.api.consumer.InboundEvent;
import me.ehp246.aufkafka.core.util.OneUtil;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class EventMDCContext {
    private EventMDCContext() {
    }

    private enum InboundContextName {
        AufKafkaFrom, AufKafkaCorrelationId, AufKafkaKey, AufKafkaEvent, AufKafkaEventMDC;
    }

    public static AutoCloseable set(final InboundEvent event) {
        if (event == null) {
            return () -> {
            };
        }
        final AutoCloseable closeable = () -> EventMDCContext.clear(event);

        MDC.put(InboundContextName.AufKafkaFrom.name(), OneUtil.toString(event.topic()));
        MDC.put(InboundContextName.AufKafkaKey.name(), event.key());

        final var propertyNames = OneUtil.toList(event.headers());
        if (propertyNames == null) {
            return closeable;
        }

        propertyNames.stream().filter(name -> name.key().startsWith(AufKafkaConstant.MDC_HEADER_PREFIX))
                .forEach(name -> MDC.put(name.key().replaceFirst(AufKafkaConstant.MDC_HEADER_PREFIX, ""),
                        new String(name.value(), StandardCharsets.UTF_8)));

        return closeable;
    }

    public static void clear(final InboundEvent msg) {
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
        propertyNames.stream().filter(name -> name.key().startsWith(AufKafkaConstant.MDC_HEADER_PREFIX))
                .forEach(name -> MDC.remove(name.key().replaceFirst(AufKafkaConstant.MDC_HEADER_PREFIX, "")));
    }
}
