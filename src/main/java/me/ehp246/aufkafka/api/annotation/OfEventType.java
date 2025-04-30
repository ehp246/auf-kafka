package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies the binding point of the value for event type header. The
 * annotation can be applied on both the producer side, i.e.,
 * {@linkplain ByKafka} interfaces, and the consumer side, i.e.,
 * {@linkplain ForEventType} classes.
 * <p>
 * Can be applied to a parameter or a method on a {@linkplain ByKafka}
 * interface.
 * <p>
 * When applied to a parameter, only {@linkplain String} type is supported.
 * <p>
 * When applied to a parameter, the specified value is ignored.
 * <p>
 *
 * @author Lei Yang
 * @since 1.0
 */
@Retention(RUNTIME)
@Target({ ElementType.METHOD, ElementType.PARAMETER })
public @interface OfEventType {
    String value() default "";
}
