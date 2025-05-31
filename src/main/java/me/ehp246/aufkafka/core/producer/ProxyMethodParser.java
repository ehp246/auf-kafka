package me.ehp246.aufkafka.core.producer;

import java.lang.reflect.Method;

/**
 *
 * @author Lei Yang
 *
 */
@FunctionalInterface
interface ProxyMethodParser {
    Parsed parse(Method method);

    record Parsed(ProxyInvocationBinder invocationBinder, ProxyReturnBinder returnBinder) {
    }
}
