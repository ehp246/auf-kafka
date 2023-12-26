package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Import;

import me.ehp246.aufkafka.api.consumer.ConsumerProvider;
import me.ehp246.aufkafka.api.consumer.InboundEndpoint;
import me.ehp246.aufkafka.api.consumer.Invocable;
import me.ehp246.aufkafka.api.consumer.InvocationListener;
import me.ehp246.aufkafka.api.consumer.Invoked.Completed;
import me.ehp246.aufkafka.api.consumer.Invoked.Failed;
import me.ehp246.aufkafka.api.consumer.MsgConsumer;
import me.ehp246.aufkafka.api.exception.UnknownKeyException;
import me.ehp246.aufkafka.core.configuration.AufKafkaConfiguration;
import me.ehp246.aufkafka.core.configuration.ConsumerConfiguration;
import me.ehp246.aufkafka.core.consumer.AnnotatedInboundConsumerRegistrar;
import me.ehp246.aufkafka.core.consumer.DefaultInvocableBinder;
import me.ehp246.aufkafka.core.consumer.DefaultInvocableScanner;
import me.ehp246.aufkafka.core.consumer.InboundEndpointConsumerConfigurer;
import me.ehp246.aufkafka.core.consumer.InboundEndpointFactory;

/**
 * @author Lei Yang
 * @since 1.0
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.TYPE)
@Import({ AufKafkaConfiguration.class, ConsumerConfiguration.class,
        AnnotatedInboundConsumerRegistrar.class, InboundEndpointFactory.class,
        InboundEndpointConsumerConfigurer.class, DefaultInvocableScanner.class,
        DefaultInvocableBinder.class })
public @interface EnableForKafka {
    /**
     * Specifies the topics to listen for inbound messages and their configurations.
     */
    Inbound[] value();

    @Target({})
    @interface Inbound {
        /**
         * Topics of the incoming messages.
         */
        From value();

        /**
         * Specifies the name to pass to {@linkplain ConsumerProvider} to retrieve a
         * {@linkplain Consumer} for this endpoint.
         */
        String consumerName() default "";

        /**
         * Specifies the packages to scan for {@linkplain ForKey} classes for this
         * consumer.
         * <p>
         * By default, the package and the sub-packages of the annotated class will be
         * scanned.
         */
        Class<?>[] scan() default {};

        /**
         * Registers the specified {@linkplain ForKey} classes explicitly.
         */
        Class<?>[] register() default {};

        /**
         * Specifies whether the listener should be started automatically.
         * <p>
         * Supports Spring property placeholder.
         */
        String autoStartup() default "true";

        /**
         * The bean name of the endpoint. Must be unique if specified.
         * <p>
         * The default name would be in the form of <code>'InboundEndpoint-${n}'</code>
         * where <code>'n'</code> is the index from {@linkplain EnableForKafka#value()}
         * starting at <code>0</code>.
         */
        String name() default "";

        /**
         * Specifies the bean name of the {@linkplain InvocationListener} type to
         * receive either {@linkplain Completed} or {@linkplain Failed} invocations on
         * this {@linkplain EnableForKafka.Inbound}.
         * <p>
         * If the execution of a {@linkplain ForKey} object on this
         * {@linkplain EnableForKafka.Inbound} completes normally, the
         * {@linkplain InvocationListener.OnCompleted#onCompleted(Completed)} will be
         * invoked.
         * <p>
         * If the execution of a {@linkplain ForKey} object on this
         * {@linkplain EnableForKafka.Inbound} throws an exception, the
         * {@linkplain InvocationListener.OnFailed#onFailed(Failed)} will be invoked.
         * <p>
         * If the invocation of the bean completes without an exception, the
         * {@linkplain ConsumerRecord} will be <strong>acknowledged</strong> to the
         * broker as a success.
         * <p>
         * {@linkplain InvocationListener.OnFailed} can throw {@linkplain Exception} in
         * which case the message will fail.
         * <p>
         * The listener bean is designed to support {@linkplain ForKey} objects. It
         * applies only after a matching {@linkplain ForKey} class has been found. E.g.,
         * the bean will not be invoked for any exception prior to
         * {@linkplain ConsumerRecord#key()} matching, i.e.,
         * {@linkplain UnknownKeyException}.
         * <p>
         * If a {@linkplain RuntimeException} happens from the bean during execution,
         * the {@linkplain ConsumerRecord} will follow broker's default failed-message
         * process.
         * <p>
         * Supports Spring property placeholder.
         */
        String invocationListener() default "";

        /**
         * Specifies the bean name of {@linkplain MsgConsumer} type to receive any
         * inbound message that no matching {@linkplain Invocable} can be found for its
         * {@linkplain ConsumerRecord#key()}.
         * <p>
         * The default value specifies a no-operation bean that logs the un-matched
         * message by {@linkplain Logger#atTrace()}. This means un-matched messages are
         * to be expected and acknowledged to the broker.
         * <p>
         * If the value is an empty string, an un-matched message will result an
         * {@linkplain UnknownKeyException}.
         * <p>
         * The setting applies to all {@linkplain InboundEndpoint}'s.
         * <p>
         * Supports Spring property placeholder.
         */
        String defaultConsumer() default "e9c593e2-37c6-48e2-8a76-67540e44e3b1";

        @Target({})
        @interface From {
            /**
             * Defines the topic name.
             * <p>
             * Supports Spring property placeholder.
             */
            String value();
        }
    }
}
