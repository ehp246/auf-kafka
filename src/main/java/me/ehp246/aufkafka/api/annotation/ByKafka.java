package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufkafka.api.producer.DirectPartitionMap;
import me.ehp246.aufkafka.api.producer.PartitionMap;
import me.ehp246.aufkafka.api.producer.ProducerConfigProvider;
import me.ehp246.aufkafka.api.producer.SerializedPartitionMap;

/**
 * Indicates that the annotated interface should be implemented by Auf Kafka as
 * a message {@linkplain Producer producer} and made available for injection.
 * <p>
 * All out-bound messages are of {@linkplain ProducerRecord
 * ProducerRecord&lt;String, String&gt;}.
 * <p>
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
	 * Specifies the destination name for out-bound {@linkplain ProducerRecord
	 * messages}.
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
	 * Specifies {@linkplain ProducerRecord#headers() header} key/value pairs for
	 * out-going messages.
	 * <p>
	 * E.g.,
	 * <p>
	 * <code>
	 *     { "AppName", "AufKafka", "AppVersion", "1.0", ... }
	 * </code>
	 * <p>
	 * Must be specified in pairs. Missing value will trigger an exception.
	 * <p>
	 * E.g., the following is missing value for property '{@code appVersion}' and
	 * will result an exception.
	 * <p>
	 * <code>
	 *     { "AppVersion" }
	 * </code>
	 * <p>
	 * Spring property placeholder is supported on values but not on keys.
	 *
	 */
	String[] headers() default {};

	/**
	 * Specifies the name to pass to {@linkplain ProducerConfigProvider} to retrieve
	 * producer configuration with which to create a {@linkplain Producer}.
	 */
	String producerConfigName() default "";

	/**
	 * Defines {@linkplain KafkaProducer} property names and values in pairs. E.g.,
	 * <p>
	 * <code>
	 *     { "buffer.memory", "0", ... }
	 * </code>
	 * <p>
	 * These properties will be passed to
	 * {@linkplain KafkaProducer#KafkaProducer(java.util.Map)}.
	 * <p>
	 * Must be specified in pairs. Missing value will trigger an exception.
	 * <p>
	 * Spring property placeholder is supported on values but not on names.
	 *
	 */
	String[] producerProperties() default {};

	/**
	 * Specifies the type of Spring bean that implements {@linkplain PartitionMap}
	 * to use with the interface to map partition key to partition.
	 * 
	 * @see SerializedPartitionMap
	 * @see DirectPartitionMap
	 */
	Class<? extends PartitionMap> partitionMap() default SerializedPartitionMap.class;
}
