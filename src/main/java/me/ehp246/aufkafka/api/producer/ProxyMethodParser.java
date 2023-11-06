package me.ehp246.aufkafka.api.producer;

import java.lang.reflect.Method;

/**
 *
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ProxyMethodParser {
    Parsed parse(Method method);

    record Parsed(ProxyInvocationBinder invocationBinder) {
    }
}
