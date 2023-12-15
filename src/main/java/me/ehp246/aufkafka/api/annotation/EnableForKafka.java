package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import me.ehp246.aufkafka.core.consumer.AnnotatedInboundConsumerRegistrar;
import me.ehp246.aufkafka.core.consumer.InboundEndpointFactory;

/**
 * @author Lei Yang
 * @since 1.0
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.TYPE)
@Import({ AnnotatedInboundConsumerRegistrar.class, InboundEndpointFactory.class })
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
         * The default name would be in the form of <code>'InboundConsumer-${n}'</code>
         * where <code>'n'</code> is the index from {@linkplain EnableForKafka#value()}
         * starting at <code>0</code>.
         */
        String name() default "";

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
