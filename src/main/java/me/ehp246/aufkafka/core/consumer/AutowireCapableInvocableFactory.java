package me.ehp246.aufkafka.core.consumer;

import java.lang.reflect.Method;
import java.util.Objects;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import me.ehp246.aufkafka.api.consumer.EventInvocableRegistry;
import me.ehp246.aufkafka.api.consumer.InboundEvent;
import me.ehp246.aufkafka.api.consumer.InstanceScope;
import me.ehp246.aufkafka.api.consumer.Invocable;
import me.ehp246.aufkafka.api.consumer.InvocableFactory;
import me.ehp246.aufkafka.api.consumer.InvocationModel;

/**
 * Creates {@linkplain Invocable} instance by
 * {@linkplain AutowireCapableBeanFactory}.
 *
 * @author Lei Yang
 * @since 1.0
 */
final class AutowireCapableInvocableFactory implements InvocableFactory {
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;
    private final EventInvocableRegistry registry;

    public AutowireCapableInvocableFactory(final AutowireCapableBeanFactory autowireCapableBeanFactory,
            final EventInvocableRegistry registry) {
        super();
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
        this.registry = registry;
    }

    @Override
    public Invocable get(final InboundEvent event) {
        Objects.requireNonNull(event);

        final var registered = this.registry.resolve(event);
        if (registered == null) {
            return null;
        }

        final Object instance = registered.scope().equals(InstanceScope.BEAN)
                ? autowireCapableBeanFactory.getBean(registered.instanceType())
                : autowireCapableBeanFactory.createBean(registered.instanceType());

        return new Invocable() {

            @Override
            public Object instance() {
                return instance;
            }

            @Override
            public Method method() {
                return registered.method();
            }

            @Override
            public InvocationModel invocationModel() {
                return registered.model();
            }

            @Override
            public void close() throws Exception {
                if (registered.scope() == InstanceScope.BEAN) {
                    return;
                }
                autowireCapableBeanFactory.destroyBean(instance);
            }
        };
    }
}
