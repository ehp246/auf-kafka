package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import me.ehp246.aufkafka.api.consumer.EventInvocable;
import me.ehp246.aufkafka.api.consumer.InboundEvent;

/**
 * Specifies the binding point for the offset value, i.e.,
 * {@linkplain ConsumerRecord#offset()}.
 * <p>
 * The annotation can be only applied on the consumer side, i.e.,
 * {@linkplain EventInvocable} classes.
 * 
 * 
 * @author Lei Yang
 * @see InboundEvent#offset()
 */
@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface OfOffset {
}
