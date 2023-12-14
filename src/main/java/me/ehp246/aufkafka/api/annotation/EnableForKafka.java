package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.sql.Connection;

/**
 * @author Lei Yang
 * @since 1.0
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.TYPE)
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
         * Specifies the name to pass to {@linkplain ConnectionFactoryProvider} to
         * eventually retrieve a {@linkplain Connection}.
         */
        String connectionFactory() default "";

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
