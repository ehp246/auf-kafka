package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.springframework.context.annotation.Import;

import me.ehp246.aufkafka.api.AufKafkaConstant;
import me.ehp246.aufkafka.api.consumer.ConsumerConfigProvider;
import me.ehp246.aufkafka.api.consumer.ConsumerExceptionListener;
import me.ehp246.aufkafka.api.consumer.Invocable;
import me.ehp246.aufkafka.api.consumer.InvocationListener;
import me.ehp246.aufkafka.api.consumer.Invoked.Completed;
import me.ehp246.aufkafka.api.consumer.Invoked.Failed;
import me.ehp246.aufkafka.api.consumer.UnmatchedConsumer;
import me.ehp246.aufkafka.core.configuration.AufKafkaConfiguration;
import me.ehp246.aufkafka.core.consumer.AnnotatedInboundEndpointRegistrar;
import me.ehp246.aufkafka.core.consumer.ConsumerConfiguration;
import me.ehp246.aufkafka.core.consumer.DefaultInvocableBinder;
import me.ehp246.aufkafka.core.consumer.DefaultInvocableScanner;
import me.ehp246.aufkafka.core.consumer.InboundEndpointConsumerConfigurer;
import me.ehp246.aufkafka.core.consumer.InboundEndpointFactory;

/**
 * Enables the consumer-side capabilities of Auf Kafka.
 * <p>
 * Mostly to declare {@linkplain Inbound} endpoints with scanning and
 * registration of {@linkplain ForKey} classes for the endpoints.
 *
 * @author Lei Yang
 * @since 1.0
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.TYPE)
@Import({ AufKafkaConfiguration.class, ConsumerConfiguration.class, AnnotatedInboundEndpointRegistrar.class,
        InboundEndpointFactory.class, InboundEndpointConsumerConfigurer.class, DefaultInvocableScanner.class,
        DefaultInvocableBinder.class })
public @interface EnableForKafka {
    /**
     * Specifies the topics to listen for in-bound messages and their
     * configurations.
     */
    Inbound[] value();

    @Target({})
    @interface Inbound {
        /**
         * Topic of the incoming messages.
         */
        From value();

        /**
         * Specifies the name to pass to {@linkplain ConsumerConfigProvider} to retrieve
         * consumer configuration with which to create a {@linkplain Consumer}.
         */
        String consumerConfigName() default "";

        /**
         * Defines {@linkplain KafkaConsumer} property names and values in pairs. E.g.,
         * <p>
         * <code>
         *     { "auto.offset.reset", "latest", ... }
         * </code>
         * <p>
         * These properties will be passed to
         * {@linkplain KafkaConsumer#KafkaConsumer(java.util.Map)}.
         * <p>
         * Must be specified in pairs. Missing value will trigger an exception.
         * <p>
         * Spring property placeholder and SpEL expression are supported on values but
         * not on names.
         *
         */
        String[] consumerProperties() default {};

        /**
         * Specifies the packages to scan for {@linkplain ForKey} classes for this
         * consumer.
         * <p>
         * By default, the package and the sub-packages of the annotated class will be
         * scanned.
         */
        Class<?>[] scan() default {};

        /**
         * Registers the specified {@linkplain ForKey} classes explicitly for this
         * endpoint.
         */
        Class<?>[] register() default {};

        /**
         * Specifies whether the listener should be started automatically.
         * <p>
         * Supports Spring property placeholder and SpEL expression.
         */
        String autoStartup() default "true";

        /**
         * The bean name of the endpoint. Must be unique if specified.
         * <p>
         * The default name would be in the form of <code>'inboundEndpoint-${n}'</code>
         * where <code>'n'</code> is the index from {@linkplain EnableForKafka#value()}
         * starting at <code>0</code>.
         */
        String name() default "";

        /**
         * Specifies the bean name of the {@linkplain InvocationListener} type to
         * receive invocation events on this {@linkplain EnableForKafka.Inbound}.
         * <p>
         * If the execution of a {@linkplain ForKey} object on this
         * {@linkplain EnableForKafka.Inbound} completes normally, the
         * {@linkplain InvocationListener.CompletedListener#onCompleted(Completed)} will
         * be invoked.
         * <p>
         * If the execution of a {@linkplain ForKey} object on this
         * {@linkplain EnableForKafka.Inbound} throws an exception, the
         * {@linkplain InvocationListener.FailedListener#onFailed(Failed)} will be
         * invoked.
         * <p>
         * {@linkplain InvocationListener.FailedListener} can throw
         * {@linkplain Exception}.
         * <p>
         * The listener bean is designed to support the invocation of
         * {@linkplain ForKey} objects. It applies only after a matching
         * {@linkplain ForKey} class has been found. It will not be invoked if there is
         * no matching {@linkplain Invocable}, e.g.,
         * {@linkplain EnableForKafka.Inbound#unmatchedConsumer()} invocation.
         * <p>
         * Supports Spring property placeholder and SpEL expression.
         */
        String invocationListener() default "";

        /**
         * Specifies the bean name of {@linkplain UnmatchedConsumer} type to accept any
         * message that no matching {@linkplain Invocable} can be found for its
         * {@linkplain ConsumerRecord#key()}.
         * <p>
         * The default value specifies a no-operation bean that logs the un-matched
         * message by {@linkplain Logger#atTrace()}. This means un-matched messages are
         * to be expected and acknowledged to the broker.
         * <p>
         * Supports Spring property placeholder and SpEL expression.
         */
        String unmatchedConsumer() default AufKafkaConstant.BEAN_NOOP_UNMATCHED_CONSUMER;

        /**
         * Specifies the bean name of {@linkplain ConsumerExceptionListener} type to
         * receive any exception that happened when consuming a message.
         * <p>
         * The default is to log and ignore.
         * <p>
         * This listener is more general purpose than {@linkplain InvocationListener}
         * which is specific for {@linkplain ForKey} invocations. An exception from
         * {@linkplain ForKey} invocation can trigger this listener on top of
         * {@linkplain InvocationListener}.
         * <p>
         * Supports Spring property placeholder and SpEL expression.
         */
        String consumerExceptionListener() default AufKafkaConstant.BEAN_IGNORING_CONSUMEREXCEPTION_LISTENER;

        @Target({})
        @interface From {
            /**
             * Defines the topic name.
             * <p>
             * Supports Spring property placeholder and SpEL expression.
             */
            String value();
        }
    }
}
