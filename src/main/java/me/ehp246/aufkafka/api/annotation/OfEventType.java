package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies the binding point of the value for event type header. The
 * annotation can be applied on both the producer side, i.e.,
 * {@linkplain ByKafka} interfaces, and the consumer side, i.e.,
 * {@linkplain ForEventType} and {@linkplain ForKey} classes.
 * <p>
 * On a {@linkplain ByKafka} interface, can be applied to either a parameter or
 * a method.
 * <p>
 * When applied to a parameter, only {@linkplain String} type is supported and
 * the specified value is ignored.
 * <p>
 * When applied to a method, the header will have the specified value if the
 * value is not empty. An empty value suppresses the header.
 * <p>
 *
 * @author Lei Yang
 * @since 1.0
 * @see ForEventType
 * @see ForKey
 * 
 */
@Retention(RUNTIME)
@Target({ ElementType.METHOD, ElementType.PARAMETER })
public @interface OfEventType {
    String value() default "";
}
