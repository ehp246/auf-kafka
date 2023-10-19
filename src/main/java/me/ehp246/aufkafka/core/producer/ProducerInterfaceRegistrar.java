package me.ehp246.aufkafka.core.producer;

import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionOverrideException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import me.ehp246.aufkafka.api.annotation.ByProducer;
import me.ehp246.aufkafka.api.annotation.EnableByProducer;
import me.ehp246.aufkafka.core.util.OneUtil;

public final class ProducerInterfaceRegistrar implements ImportBeanDefinitionRegistrar {
    private final static Logger LOGGER = LogManager.getLogger();

    @Override
    public void registerBeanDefinitions(final AnnotationMetadata metadata, final BeanDefinitionRegistry registry) {
        LOGGER.atTrace().log("Scanning for {}", ByProducer.class::getCanonicalName);

        for (final var found : new ProducerInterfaceScanner(EnableByProducer.class, ByProducer.class, metadata)
                .perform()
                .collect(Collectors.toList())) {

            final Class<?> producerInterface;
            try {
                producerInterface = Class.forName(found.getBeanClassName());
            } catch (final ClassNotFoundException ignored) {
                // Class scanning started this. Should not happen.
                throw new RuntimeException("Class scanning started this. Should not happen.");
            }

            final var beanName = OneUtil.producerInterfaceBeanName(producerInterface);
            final var proxyBeanDefinition = this.getProxyBeanDefinition(
                    metadata.getAnnotationAttributes(EnableByProducer.class.getCanonicalName()), producerInterface);

            if (registry.containsBeanDefinition(beanName)) {
                throw new BeanDefinitionOverrideException(beanName, proxyBeanDefinition,
                        registry.getBeanDefinition(beanName));
            }

            registry.registerBeanDefinition(beanName, proxyBeanDefinition);
        }
    }

    private BeanDefinition getProxyBeanDefinition(final Map<String, Object> map, final Class<?> byRestInterface) {
        final var args = new ConstructorArgumentValues();

        args.addGenericArgumentValue(byRestInterface);

        final var beanDef = new GenericBeanDefinition();
        beanDef.setBeanClass(byRestInterface);
        beanDef.setConstructorArgumentValues(args);
        beanDef.setFactoryBeanName(ByRestProxyFactory.class.getName());
        beanDef.setFactoryMethodName("newInstance");
        beanDef.setResourceDescription(byRestInterface.getCanonicalName());

        return beanDef;
    }
}
