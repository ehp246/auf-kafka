package me.ehp246.aufkafka.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;

/**
 * *
 *
 * @author Lei Yang
 * @since 1.0
 * @see Parameter#getName()
 * @see <a href='https://openjdk.org/jeps/118'>JEP 118: Access to Parameter
 *      Names at Runtime</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface OfHeader {
    /**
     * The name of the header.
     */
    String value() default "";
}
