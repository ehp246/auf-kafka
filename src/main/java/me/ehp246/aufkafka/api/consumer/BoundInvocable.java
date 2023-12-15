package me.ehp246.aufkafka.api.consumer;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import me.ehp246.aufkafka.api.consumer.Invoked.Completed;
import me.ehp246.aufkafka.api.consumer.Invoked.Failed;

/**
 * An {@linkplain Invocable} that has been bound to a
 * {@linkplain ConsumerRecord}, ready to be invoked.
 *
 * @author Lei Yang
 * @since 1.0
 */
public interface BoundInvocable {
    Invocable invocable();

    ConsumerRecord<?, ?> msg();

    /**
     * Resolved arguments. Might not contain a value but the array should never be
     * <code>null</code>.
     */
    Object[] arguments();

    Map<String, String> log4jContext();

    default Invoked invoke() {
        try {
            final var invocable = this.invocable();
            final var returned = invocable.method().invoke(invocable.instance(), this.arguments());
            return new Completed() {

                @Override
                public BoundInvocable bound() {
                    return BoundInvocable.this;
                }

                @Override
                public Object returned() {
                    return returned;
                }
            };
        } catch (final InvocationTargetException e) {
            return new Failed() {

                @Override
                public BoundInvocable bound() {
                    return BoundInvocable.this;
                }

                @Override
                public Throwable thrown() {
                    return e.getCause();
                }
            };
        } catch (final Exception e) {
            return new Failed() {

                @Override
                public BoundInvocable bound() {
                    return BoundInvocable.this;
                }

                @Override
                public Throwable thrown() {
                    return e;
                }
            };
        }
    }
}
