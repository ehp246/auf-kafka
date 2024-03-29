package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author Lei Yang
 *
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface Applying {
}
