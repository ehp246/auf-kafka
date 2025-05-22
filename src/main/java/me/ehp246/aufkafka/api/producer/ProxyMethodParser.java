package me.ehp246.aufkafka.api.producer;

import java.lang.reflect.Method;

/**
 *
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ProxyMethodParser {
    ProducerProxyInvocationBinder parse(Method method);
}
