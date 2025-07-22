package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.Instant;

import me.ehp246.aufkafka.api.consumer.EventInvocable;

/**
 * Specifies the binding point for the timestamp value. The annotation can be
 * applied on both the producer side, i.e., {@linkplain ByKafka} interfaces, and
 * the consumer side, i.e., {@linkplain EventInvocable} classes.
 * <p>
 * Supported types:
 * <ul>
 * <li>{@linkplain Instant}</li>
 * <li>{@linkplain Long}</li>
 * <li><code>long</code></li>
 * </ul>
 * 
 * @author Lei Yang
 *
 */
@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface OfTimestamp {
}
