package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;

import me.ehp246.aufkafka.api.consumer.EventInvocable;
import me.ehp246.aufkafka.api.consumer.InboundEvent;
import me.ehp246.aufkafka.api.producer.OutboundEvent;

/**
 * Specifies the binding point for the partition value. The annotation can be
 * applied on both the producer side, i.e., {@linkplain ByKafka} interfaces, and
 * the consumer side, i.e., {@linkplain EventInvocable} classes.
 * <p>
 * On the producer side, it binds the value to
 * {@linkplain ProducerRecord#partition()}.
 * <p>
 * When applied to a parameter on a {@linkplain ByKafka} interface, the
 * following types are supported:
 * <ul>
 * <li>{@linkplain Integer}</li>
 * <li><code>int</code></li>
 * </ul>
 * <p>
 * When applied to a method, all out-going messages will have the specified
 * partition value unless it's overridden by an annotated parameter. In this
 * case, the value must not be negative. Negative value means <code>null</code>
 * for partition.
 * <p>
 * On the consumer side, when applied to a parameter of the
 * {@linkplain Applying} method, it specifies the injection point for
 * {@linkplain ConsumerRecord#partition()}. Types that are
 * {@linkplain Class#isAssignableFrom(Class)} from {@linkplain Integer} is
 * supported.
 * <p>
 * 
 * @author Lei Yang
 * @see OutboundEvent#partition()
 * @see InboundEvent#partition()
 *
 */
@Documented
@Retention(RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.METHOD })
public @interface OfPartition {
    int value() default -1;
}
