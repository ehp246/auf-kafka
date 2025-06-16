package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufkafka.api.consumer.EventInvocable;
import me.ehp246.aufkafka.api.consumer.InboundEvent;
import me.ehp246.aufkafka.api.serializer.jackson.FromJson;

/**
 * Specifies which method should be invoked on a {@linkplain EventInvocable}
 * class.
 * <p>
 * The method must be <code>public</code>. The method can be inherited. But
 * there must be only one.
 * <p>
 * The following types of arguments can be injected without annotation on
 * invocation:
 * <ul>
 * <li>{@linkplain InboundEvent}</li>
 * <li>{@linkplain ConsumerRecord}</li>
 * <li>{@linkplain FromJson}</li>
 * </ul>
 * <p>
 * De-serializing by {@linkplain JsonView} is supported on message payload.
 * 
 * @author Lei Yang
 * @see ForEvent
 * @see ForKey
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface Applying {
}
