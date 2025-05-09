package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import me.ehp246.aufkafka.api.AufKafkaConstant;
import me.ehp246.aufkafka.api.consumer.Invocable;

/**
 * Indicates that the class contains logic that should be applied upon receiving
 * a message by matching on the header keys and values.
 * <p>
 * The annotated class must be <code>public</code>.
 * <p>
 *
 * @author Lei Yang
 * @since 1.0
 * @see Applying
 * @see Invocable
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface ForHeader {
    String[] value() default {};

    String[] key() default { AufKafkaConstant.HEADER_KEY_EVENT_TYPE };

    Execution execution() default @Execution();
}
