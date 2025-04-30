package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies the binding point for the key of the record. The annotation can be
 * applied on both the producer side, i.e., {@linkplain ByKafka} interfaces, and
 * the consumer side, i.e., {@linkplain ForKey} {@linkplain Applying} methods.
 * <p>
 * On a {@linkplain ByKafka} interface:
 * <ul>
 * <li>When applied to a parameter, the argument will be converted to
 * {@linkplain String} via {@linkplain Object#toString()}.
 * {@linkplain OfKey#value()} is ignored.</li>
 * <p>
 * <li>When applied to a method of {@linkplain ByKafka} interfaces, a
 * {@linkplain ByKafka#value() value} of {@linkplain String#isBlank()} sets the
 * key to <code>null</code>.</li>
 * <p>
 * <ul>
 * 
 * @author Lei Yang
 * @since 1.0
 * 
 */
@Retention(RUNTIME)
@Target({ ElementType.METHOD, ElementType.PARAMETER })
public @interface OfKey {
    String value() default "";
}
