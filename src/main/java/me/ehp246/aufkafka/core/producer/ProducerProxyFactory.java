package me.ehp246.aufkafka.core.producer;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.EnableByKafka;
import me.ehp246.aufkafka.api.producer.ProducerFnProvider;
import me.ehp246.aufkafka.api.producer.ProducerFnProvider.ProducerFnConfig;
import me.ehp246.aufkafka.api.producer.ProxyInvocationBinder;
import me.ehp246.aufkafka.api.producer.ProxyMethodParser;
import me.ehp246.aufkafka.api.spi.ExpressionResolver;

/**
 *
 * @author Lei Yang
 * @see EnableByKafka
 * @see ProducerProxyRegistrar
 * @since 1.0
 */
public final class ProducerProxyFactory {
    private static final Map<Method, ProxyInvocationBinder> parsedCache = new ConcurrentHashMap<>();

    private final ProxyMethodParser methodParser;
    private final ProducerFnProvider producerFnProvider;
    private final ExpressionResolver expressionResolver;

    public ProducerProxyFactory(final ProxyMethodParser methodParser, final ProducerFnProvider producerFnProvider,
            final ExpressionResolver expressionResolver) {
        super();
        this.methodParser = methodParser;
        this.producerFnProvider = producerFnProvider;
        this.expressionResolver = expressionResolver;
    }

    @SuppressWarnings("unchecked")
    public <T> T newInstance(final Class<T> proxyInterface) {
        final var byKafka = proxyInterface.getAnnotation(ByKafka.class);

        final var producerFn = producerFnProvider
                .get(new ProducerFnConfig(byKafka.producerName(), byKafka.partitionFn(),
                        producerProperties(Arrays.asList(byKafka.producerProperties()), byKafka.name())));

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

    private Map<String, Object> producerProperties(final List<String> properties, final String beanName) {
        if ((properties.size() & 1) != 0) {
            throw new IllegalArgumentException(
                    "Producer properties should be in name/value pair on '" + beanName + "'");
        }

        final Map<String, Object> resolvedProperties = new HashMap<>();
        for (int i = 0; i < properties.size(); i += 2) {
            resolvedProperties.put(properties.get(i), expressionResolver.apply(properties.get(i + 1)));
        }
        return resolvedProperties;
    }
}
