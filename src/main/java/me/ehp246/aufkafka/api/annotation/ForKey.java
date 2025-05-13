package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import me.ehp246.aufkafka.api.consumer.EventInvocableLookupType;
import me.ehp246.aufkafka.api.consumer.Invocable;

/**
 * Indicates that the class defines methods that should be invoked on an event
 * by matching on message's key, i.e., {@linkplain ConsumerRecord#key()}.
 * <p>
 * The annotated class must be <code>public</code>.
 * <p>
 * 
 * @author Lei Yang
 * @since 1.0
 * @see Applying
 * @see EventInvocableLookupType
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.TYPE)
public @interface ForKey {
    /**
     * The matching is done via {@linkplain String#matches(String)} where the
     * <code>this</code> object is from {@linkplain ConsumerRecord#key()} and the
     * argument is the value specified here which could be a regular expression.
     * <p>
     * When multiple values are specified, the matching follows the declaration
     * order. Any matched value could trigger invocation. I.e., multiple expressions
     * are considered logical <code>||</code>.
     * <p>
     * If no value is specified, the class' simple name, i.e.,
     * {@linkplain Class#getSimpleName()}, is used as the default.
     * <p>
     * The key matching is done without a defined order. Overlapping expressions
     * from multiple {@linkplain ForKey#value()}'s might result in un-deterministic
     * behavior.
     */
    String[] value() default {};

    /**
     * Specifies the execution model of the {@linkplain Invocable}.
     * 
     */
    Execution execution() default @Execution();
}
