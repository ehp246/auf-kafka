package me.ehp246.aufkafka.core.producer;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.EnableByKafka;
import me.ehp246.aufkafka.api.producer.ProducerFnProvider;
import me.ehp246.aufkafka.api.producer.ProxyInvocationBinder;
import me.ehp246.aufkafka.api.producer.ProxyMethodParser;

/**
 *
 * @author Lei Yang
 * @see EnableByKafka
 * @see ProxyRegistrar
 * @since 1.0
 */
public final class ProxyFactory {
    private static final Map<Method, ProxyInvocationBinder> parsedCache = new ConcurrentHashMap<>();

    private final ProxyMethodParser methodParser;
    private final ProducerFnProvider producerFnProvider;

    public ProxyFactory(final ProxyMethodParser methodParser, final ProducerFnProvider producerFnProvider) {
        super();
        this.methodParser = methodParser;
        this.producerFnProvider = producerFnProvider;
    }

    @SuppressWarnings("unchecked")
    public <T> T newInstance(final Class<T> proxyInterface) {
        final var byKafka = proxyInterface.getAnnotation(ByKafka.class);

        final var producerFn = producerFnProvider.get(byKafka.configName());

        return (T) Proxy.newProxyInstance(proxyInterface.getClassLoader(), new Class[] { proxyInterface },
                new InvocationHandler() {
                    private final int hashCode = new Object().hashCode();

                    @Override
                    public Object invoke(final Object proxy, final Method method, final Object[] args)
                            throws Throwable {
                        if (method.getName().equals("toString")) {
                            return proxyInterface.toString();
                        }
                        if (method.getName().equals("hashCode")) {
                            return hashCode;
                        }
                        if (method.getName().equals("equals")) {
                            return proxy == args[0];
                        }

                        if (method.isDefault()) {
                            return MethodHandles.privateLookupIn(proxyInterface, MethodHandles.lookup())
                                    .findSpecial(proxyInterface, method.getName(),
                                            MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
                                            proxyInterface)
                                    .bindTo(proxy).invokeWithArguments(args);
                        }

                        final var event = parsedCache.computeIfAbsent(method, m -> methodParser.parse(method))
                                .apply(proxy, args);

                        producerFn.send(event);

                        return null;
                    }
                });
    }
}
