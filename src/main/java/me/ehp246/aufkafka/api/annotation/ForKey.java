package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import me.ehp246.aufkafka.api.consumer.InstanceScope;
import me.ehp246.aufkafka.api.consumer.InvocationModel;

/**
 * Indicates that the class defines methods that should be invoked on a message
 * by matching on message's key, i.e., {@linkplain ConsumerRecord#key()}.
 * <p>
 * The method that the record is to be applied to is determined by the following
 * lookup process:
 * <ul>
 * <li>Only <code>public</code> methods declared directly on the class are
 * considered. No inherited.
 * <li>a method annotated by {@linkplain Applying}, or...
 * <li>a method named '<code>invoke</code>', or...
 * <li>a method named '<code>apply</code>'.
 * </ul>
 * The signature and the declaration order are not considered. The first found
 * is accepted. If no method is found, it's an exception.
 * <p>
 * If the inbound's {@linkplain ConsumerRecord#key()} is specified and the
 * invocation is successful, a reply message will be sent to the destination.
 * The message will have:
 * <ul>
 * <li>the same key
 * <li>the same correlation id
 * <li>the return object as value. <code>null</code> if the method has no
 * return.
 * </ul>
 *
 * @author Lei Yang
 * @since 1.0
 * @see Applying
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.TYPE)
public @interface ForKey {
    /**
     * Specifies the message keys for which a method of the class should be applied.
     * <p>
     * The matching is done via {@linkplain String#matches(String)} where the
     * <code>this</code> object is from {@linkplain ConsumerRecord#key()} and the
     * argument is the value specified here which could be a regular expression.
     * <p>
     * When multiple values are specified, the matching follows the declaration
     * order. Any single value could trigger invocation. I.e., multiple expressions
     * are considered logical <code>||</code>.
     * <p>
     * If no value is specified, the class' simple name, i.e.,
     * {@linkplain Class#getSimpleName()}, is used as the default.
     * <p>
     * The key matching is done without a defined order. Overlapping expressions
     * from multiple {@linkplain ForKey}'s might result in un-deterministic
     * behavior.
     */
    String[] value() default {};

    /**
     * Specifies how to instantiate an instance of the class.
     *
     * @see InstanceScope
     */
    InstanceScope scope() default InstanceScope.MESSAGE;

    InvocationModel invocation() default InvocationModel.DEFAULT;
}
