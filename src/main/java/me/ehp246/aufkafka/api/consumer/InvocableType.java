package me.ehp246.aufkafka.api.consumer;

import java.lang.reflect.Method;

/**
 * 
 * @author Lei Yang
 *
 */
public record InvocableType(Class<?> instanceType, Method method, InstanceScope scope,
        InvocationModel model) {
}
