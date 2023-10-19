package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import me.ehp246.aufkafka.core.producer.ProducerInterfaceRegistrar;

/**
 * Enables Auf REST's annotation-driven REST-proxing capability for client-side
 * applications. It imports infrastructure beans and scans the class path for
 * {@link me.ehp246.aufrest.api.annotation.ByRest ByRest}-annotated interfaces
 * making them available to be injected as Spring beans.
 * <p>
 * By default, the package and the sub-packages of the annotated class will be
 * scanned.
 *
 * @author Lei Yang
 * @since 1.0
 */
@Retention(RUNTIME)
@Target(TYPE)
@Import({ ProducerInterfaceRegistrar.class })
public @interface EnableByProducer {
    /**
     * Specifies the packages to scan for annotated
     * {@link me.ehp246.aufrest.api.annotation.ByRest ByRest} interfaces. The
     * package of each class specified will be scanned.
     * <p>
     * Once specified, the element turns off the default scanning.
     */
    Class<?>[] scan() default {};
}
