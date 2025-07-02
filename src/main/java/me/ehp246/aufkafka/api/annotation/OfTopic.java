package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import me.ehp246.aufkafka.api.consumer.EventInvocable;

/**
 * Specifies the binding point for the topic of the record. The annotation can
 * be applied on both the producer side, i.e., {@linkplain ByKafka} interfaces,
 * and the consumer side, i.e., {@linkplain EventInvocable}
 * {@linkplain Applying} methods.
 * 
 * @author Lei Yang
 * @since 1.0
 *
 */
@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface OfTopic {
}
