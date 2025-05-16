package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import me.ehp246.aufkafka.api.consumer.EventInvocableKeyType;
import me.ehp246.aufkafka.api.consumer.EventInvocable;

/**
 * Indicates that the class defines methods that should be invoked on an event
 * message by matching on event header as defined by
 * {@linkplain EnableForKafka.Inbound#eventHeader()}.
 * <p>
 * The annotated class must be <code>public</code>.
 * <p>
 *
 * @author Lei Yang
 * @since 1.0
 * @see Applying
 * @see EnableForKafka.Inbound#eventHeader()
 * @see EventInvocableKeyType#EVENT_HEADER
 */
@Retention(RUNTIME)
@Target({ ElementType.TYPE })
public @interface ForEvent {
    /**
     * Specifies the values of the header to match.
     * <p>
     * The matching is done via {@linkplain String#matches(String)} where the
     * <code>this</code> object is from {@linkplain ConsumerRecord#headers()} and
     * the argument is the value specified here which could be a regular expression.
     * <p>
     * When multiple values are specified, the matching follows the declaration
     * order. Any matched value could trigger invocation. I.e., multiple expressions
     * are considered logical <code>||</code>.
     * <p>
     * If no value is specified, the class' simple name, i.e.,
     * {@linkplain Class#getSimpleName()}, is used as the default.
     * <p>
     * The matching is done without a defined order. Overlapping expressions from
     * multiple {@linkplain ForEvent#value()}'s might result in un-deterministic
     * behavior.
     */
    String[] value() default {};

    /**
     * Specifies the execution model of the {@linkplain EventInvocable}.
     * 
     */
    Execution execution() default @Execution();
}
