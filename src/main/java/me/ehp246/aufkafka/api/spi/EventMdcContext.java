package me.ehp246.aufkafka.api.spi;

import java.util.Collections;
import java.util.Set;

import org.slf4j.MDC;

import me.ehp246.aufkafka.api.common.AufKafkaConstant;
import me.ehp246.aufkafka.api.consumer.InboundEvent;
import me.ehp246.aufkafka.core.util.OneUtil;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class EventMdcContext {
    /**
     * Never <code>null</code>.
     */
    private static final ThreadLocal<Set<String>> MDC_HEADERS = ThreadLocal.withInitial(Set::of);

    private EventMdcContext() {
    }

    private enum InboundContextName {
        AufKafkaFrom, AufKafkaCorrelationId, AufKafkaKey, AufKafkaEvent, AufKafkaEventMDC;
    }

    public static AutoCloseable setMdcHeaders(final Set<String> keys) {
        MDC_HEADERS.set(keys == null ? Set.of() : Collections.unmodifiableSet(keys));
        return EventMdcContext::clearMdcHeaders;
    }

    public static void clearMdcHeaders() {
        MDC_HEADERS.remove();
    }

    /**
     * Returned {@linkplain AutoCloseable} must be executed on the same thread as
     * invoking this method.
     */
    public static AutoCloseable set(final InboundEvent event) {
        if (event == null) {
            return () -> {
            };
        }
        final AutoCloseable closeable = () -> EventMdcContext.clear(event);

        MDC.put(InboundContextName.AufKafkaFrom.name(), OneUtil.toString(event.topic()));
        MDC.put(InboundContextName.AufKafkaKey.name(), event.key());

        final var headerMap = event.headerMap();
        if (headerMap == null || headerMap.isEmpty()) {
            return closeable;
        }
        final var keyNames = headerMap.keySet();

        keyNames.stream().filter(name -> name.startsWith(AufKafkaConstant.MDC_HEADER_PREFIX)).forEach(name -> MDC
                .put(name.replaceFirst(AufKafkaConstant.MDC_HEADER_PREFIX, ""), OneUtil.toString(headerMap.get(name))));

        MDC_HEADERS.get().stream().forEach(name -> MDC.put(name, OneUtil.toString(headerMap.get(name))));

        return closeable;
    }

    public static void clear(final InboundEvent event) {
        MDC_HEADERS.get().stream().forEach(MDC::remove);

        if (event == null) {
            return;
        }

        for (final var value : InboundContextName.values()) {
            MDC.remove(value.name());
        }

        final var propertyNames = event.headerList();
        if (propertyNames == null || propertyNames.isEmpty()) {
            return;
        }

        propertyNames.stream().filter(name -> name.key().startsWith(AufKafkaConstant.MDC_HEADER_PREFIX))
                .forEach(name -> MDC.remove(name.key().replaceFirst(AufKafkaConstant.MDC_HEADER_PREFIX, "")));

    }
}
