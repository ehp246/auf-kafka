package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Indicates that the parameter specifies the value object.
 * <p>
 * The annotation is required on both {@linkplain ByKafka} interfaces and
 * {@linkplain ForKey} classes to send/receive the parameter as the value of the
 * message.
 * <p>
 * Serializing by {@linkplain JsonView}.
 * 
 * @author Lei Yang
 * @since 1.0
 */
@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface OfValue {
}
