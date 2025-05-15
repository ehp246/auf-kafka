package me.ehp246.aufkafka.api.exception;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import me.ehp246.aufkafka.api.annotation.Applying;
import me.ehp246.aufkafka.api.annotation.ForKey;
import me.ehp246.aufkafka.api.consumer.EventInvocableBinder;

/**
 * Thrown when {@linkplain EventInvocableBinder} doesn't know how to bind a parameter
 * on a {@linkplain ForKey} {@linkplain Applying} method to
 * {@linkplain ConsumerRecord} .
 * 
 * @author Lei Yang
 * @since 1.0
 */
public class UnboundParameterException extends RuntimeException {
    private static final long serialVersionUID = 3039734971658806609L;

    private final Parameter parameter;
    private final Method method;

    public UnboundParameterException(final Parameter parameter, final Method method) {
        super("Unbound parameter '" + parameter + "' on method '" + method + "'");
        this.parameter = parameter;
        this.method = method;
    }
}
