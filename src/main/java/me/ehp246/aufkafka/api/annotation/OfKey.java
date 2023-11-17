package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * Specifies the binding point for the value of
 * {@linkplain ProducerRecord#key()}. The annotation can be applied on
 * both the producer side, i.e., {@linkplain ByKafka} interfaces, and the consumer
 * side.
 * <p>
 * Can be applied to a parameter or a method on a {@linkplain ByKafka} interface.
 * <p>
 * When applied to a parameter, the argument will be converted to {@linkplain String} via {@linkplain Object#toString()}.
 * <p>
 * When applied to a parameter, {@linkplain OfKey#value()} is ignored.
 * <p>
 * When applied to a method of {@linkplain ByKafka} interfaces, a {@linkplain ByKafka#value() value} of {@linkplain String#isBlank()} sets the key to <code>null</code>.
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
