package me.ehp246.aufkafka.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import me.ehp246.aufkafka.api.consumer.InstanceScope;
import me.ehp246.aufkafka.api.consumer.InvocationModel;

@Retention(RUNTIME)
@Target({})
public @interface Execution {
    /**
     * Specifies how to instantiate an instance of the class.
     *
     * @see InstanceScope
     */
    InstanceScope scope() default InstanceScope.EVENT;

    /**
     * Not implemented.
     */
    InvocationModel invocation() default InvocationModel.DEFAULT;

}
