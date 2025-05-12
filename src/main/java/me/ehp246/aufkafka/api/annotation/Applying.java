package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufkafka.api.consumer.Invocable;

/**
 * Specifies which method should be invoked on a {@linkplain Invocable} class.
 * <p>
 * The method must be <code>public</code>. The method can be inherited. But
 * there must be only one.
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
