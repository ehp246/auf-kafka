package me.ehp246.aufkafka.api.spi;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.ThreadContext;

import me.ehp246.aufkafka.api.AufKafkaConstant;
import me.ehp246.aufkafka.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
public final class Log4jContext {
    private Log4jContext() {
    }

    private enum InboundContextName {
        AufKafkaFrom, AufKafkaCorrelationId, AufKafkaKey, AufKafkaLog4jThreadContext;
    }

    public static AutoCloseable set(final ConsumerRecord<String, String> msg) {
        if (msg == null) {
            return () -> {
            };
        }
        final AutoCloseable closeable = () -> Log4jContext.clear(msg);

        ThreadContext.put(InboundContextName.AufKafkaFrom.name(), OneUtil.toString(msg.topic()));
        ThreadContext.put(InboundContextName.AufKafkaKey.name(), msg.key());

        final var propertyNames = OneUtil.toList(msg.headers());
        if (propertyNames == null) {
            return closeable;
        }

        propertyNames.stream()
                .filter(name -> name.key().startsWith(AufKafkaConstant.LOG4J_CONTEXT_HEADER_PREFIX))
                .forEach(name -> ThreadContext.put(
                        name.key().replaceFirst(AufKafkaConstant.LOG4J_CONTEXT_HEADER_PREFIX, ""),
                        new String(name.value(), StandardCharsets.UTF_8)));

        return closeable;
    }

    public static void clear(final ConsumerRecord<String, String> msg) {
        if (msg == null) {
            return;
        }

        for (final var value : InboundContextName.values()) {
            ThreadContext.remove(value.name());
        }

        final var propertyNames = OneUtil.toList(msg.headers());
        if (propertyNames == null) {
            return;
        }
        propertyNames.stream()
                .filter(name -> name.key().startsWith(AufKafkaConstant.LOG4J_CONTEXT_HEADER_PREFIX))
                .forEach(name -> ThreadContext.remove(
                        name.key().replaceFirst(AufKafkaConstant.LOG4J_CONTEXT_HEADER_PREFIX, "")));
    }
}
