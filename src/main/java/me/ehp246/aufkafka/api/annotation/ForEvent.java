package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import me.ehp246.aufkafka.api.consumer.EventInvocableNameSource;

/**
 * Indicates that the class defines methods that should be invoked on a event
 * message by matching on message's event header.
 * <p>
 * The annotated class must be <code>public</code>.
 * <p>
 *
 * @author Lei Yang
 * @since 1.0
 * @see Applying
 * @see EnableForKafka.Inbound#eventHeader()
 * @see EventInvocableNameSource#EVENT_HEADER
 */
@Retention(RUNTIME)
@Target({ ElementType.TYPE })
public @interface ForEvent {
    String[] value() default {};

    Execution execution() default @Execution();
}
