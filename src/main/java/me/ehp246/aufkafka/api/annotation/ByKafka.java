package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufkafka.api.producer.DirectPartitionMap;
import me.ehp246.aufkafka.api.producer.PartitionMap;
import me.ehp246.aufkafka.api.producer.SerializedPartitionMap;

/**
 * Serializing by {@linkplain JsonView} is supported on the body.
 *
 * @author Lei Yang
 * @since 1.0
 */
@Documented
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ByKafka {
    /**
     * Specifies the destination name for out-bound messages.
     */
    String value();

    /**
     * Specifies a bean name by which the interface can be injected.
     * <p>
     * The default is from {@link Class#getSimpleName()} with the first letter in
     * lower-case.
     *
     * @return the bean name of the interface.
     * @see Qualifier
     */
    String name() default "";

    /**
     * Specifies the type of Spring bean that implements {@linkplain PartitionMap}
     * to use with the interface to map partition key to partition.
     * 
     * @see SerializedPartitionMap
     * @see DirectPartitionMap
     */
    Class<? extends PartitionMap> partitionMap() default SerializedPartitionMap.class;
}
