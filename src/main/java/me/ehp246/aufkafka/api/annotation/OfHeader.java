package me.ehp246.aufkafka.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.producer.ProducerRecord;

import me.ehp246.aufkafka.api.consumer.EventInvocable;

/**
 * Specifies the binding point for a custom header. The annotation can be
 * applied on both the producer side, i.e., {@linkplain ByKafka} interfaces, and
 * the consumer side, i.e., methods of {@linkplain EventInvocable} classes.
 * <p>
 * On the producer side, when applied to a parameter on a {@linkplain ByKafka}
 * interface, it specifies the key and argument of a header for the out-going
 * message.
 * <p>
 * When applied to a method of {@linkplain ByKafka} interface, it specifies both
 * header keys and values for all out-going message from the method.
 * <p>
 * For out-going messages, all header values will be converted to
 * {@linkplain String} via {@linkplain Object#toString()} then to
 * <code>byte []</code> via {@linkplain String#getBytes()} with
 * {@linkplain StandardCharsets#UTF_8}.
 * <p>
 * If the argument is <code>null</code>, the header will have a value of
 * zero-length <code>byte []</code>.
 * <p>
 * On the consumer side, when applied to a parameter of a
 * {@linkplain ForEvent}/{@linkplain ForKey} {@linkplain Applying} method, it
 * specifies the injection point for the value of the named header of the
 * in-bound message.
 * <p>
 * There is no effect if applied to a method on the consumer side.
 * <p>
 * For {@linkplain String} parameters, <code>byte []</code> values are converted
 * by {@linkplain StandardCharsets#UTF_8}.
 * <p>
 * When {@linkplain OfHeader#value()} is not specified, the header key is
 * inferred from the parameter name with the first letter capitalized. For the
 * inferencing to work properly, '<code>-parameters</code>' compiler option is
 * probably desired.
 *
 * @author Lei Yang
 * @since 1.0
 * @see Parameter#getName()
 * @see <a href='https://openjdk.org/jeps/118'>JEP 118: Access to Parameter
 *      Names at Runtime</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.METHOD })
public @interface OfHeader {
    /**
     * Specifies {@linkplain ProducerRecord#headers() header} key/value pairs.
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
     * Spring property placeholder and SpEL expression are supported on values but
     * not on keys.
     *
     */
    String[] value() default {};
}
