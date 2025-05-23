package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import me.ehp246.aufkafka.api.producer.PartitionFn;

/**
 * @author Lei Yang
 *
 */
@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface OfPartition {
}
