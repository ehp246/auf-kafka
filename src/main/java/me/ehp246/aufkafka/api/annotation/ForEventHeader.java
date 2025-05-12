package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that the class defines methods that should be invoked on a message
 * by matching on message's event type.
 * <p>
 * The annotated class must be <code>public</code>.
 * <p>
 *
 * @author Lei Yang
 * @since 1.0
 * @see Applying
 */
@Retention(RUNTIME)
@Target({ ElementType.TYPE })
public @interface ForEventHeader {
    String[] value() default {};

    Execution execution() default @Execution();
}
