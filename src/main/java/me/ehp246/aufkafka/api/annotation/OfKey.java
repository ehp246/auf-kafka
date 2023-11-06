package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author Lei Yang
 *
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface OfKey {
    String value() default "";
}
