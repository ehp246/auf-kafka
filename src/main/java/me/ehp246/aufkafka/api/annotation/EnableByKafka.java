package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import me.ehp246.aufkafka.core.configuration.AufKafkaConfiguration;
import me.ehp246.aufkafka.core.producer.DefaultProducerRecordBuilder;
import me.ehp246.aufkafka.core.producer.DefaultProxyMethodParser;
import me.ehp246.aufkafka.core.producer.ProxyConfiguration;
import me.ehp246.aufkafka.core.producer.ProxyFactory;
import me.ehp246.aufkafka.core.producer.ProxyRegistrar;

/**
 * By default, the package and the sub-packages of the annotated class will be
 * scanned.
 *
 * @author Lei Yang
 * @since 1.0
 */
@Retention(RUNTIME)
@Target(TYPE)
@Import({ AufKafkaConfiguration.class, ProxyConfiguration.class, ProxyRegistrar.class,
        ProxyFactory.class, DefaultProxyMethodParser.class, DefaultProducerRecordBuilder.class })
public @interface EnableByKafka {
    /**
     * Specifies the packages to scan for annotated {@link ByKafka} interfaces. The
     * package of each class specified will be scanned.
     * <p>
     * Once specified, the element turns off the default scanning.
     */
    Class<?>[] scan() default {};
}
