package me.ehp246.aufkafka.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;

/**
 * Specifies the binding point for a custom header. The annotation can be
 * applied on both the producer side, i.e., {@linkplain ByKafka} interfaces, and
 * the consumer side, i.e., {@linkplain ForKey} classes.
 * <p>
 * On the producer side, applied to a parameter on a {@linkplain ByKafka}
 * interface, it specifies the name and argument of a header for the out-going
 * message.
 * <p>
 * For out-going messages, all header values will be converted to
 * {@linkplain String} via {@linkplain Object#toString()} then to
 * <code>byte []</code> via {@linkplain String#getBytes()} with
 * {@linkplain StandardCharsets#UTF_8}.
 * <p>
 * If the argument is <code>null</code>, the header will have a value of
 * zero-length <code>byte []</code>.
 * <p>
 * On the consumer side, applied to a parameter of a {@linkplain ForKey}
 * {@linkplain Applying} method, it specifies the injection point for the value
 * of the named header of the in-coming message.
 * <p>
 * For {@linkplain String} parameters, <code>byte []</code> values are converted
 * by {@linkplain StandardCharsets#UTF_8}.
 * <p>
 * When {@linkplain OfHeader#value()} is not specified, the header name is
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
@Target({ ElementType.PARAMETER })
public @interface OfHeader {
    /**
     * The name of the header.
     */
    String value() default "";
}
