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

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.EnableByKafka;
import me.ehp246.aufkafka.core.util.OneUtil;

public final class ProducerProxyRegistrar implements ImportBeanDefinitionRegistrar {
    private final static Logger LOGGER = LogManager.getLogger();

    @Override
    public void registerBeanDefinitions(final AnnotationMetadata metadata, final BeanDefinitionRegistry registry) {
        LOGGER.atTrace().log("Scanning for {}", ByKafka.class::getCanonicalName);

        for (final var found : new ProducerInterfaceScanner(EnableByKafka.class, ByKafka.class, metadata)
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
                    metadata.getAnnotationAttributes(EnableByKafka.class.getCanonicalName()), producerInterface);

            if (registry.containsBeanDefinition(beanName)) {
                throw new BeanDefinitionOverrideException(beanName, proxyBeanDefinition,
                        registry.getBeanDefinition(beanName));
            }

            registry.registerBeanDefinition(beanName, proxyBeanDefinition);
        }
    }

    private BeanDefinition getProxyBeanDefinition(final Map<String, Object> map, final Class<?> byKafkaInterface) {
        final var args = new ConstructorArgumentValues();

        args.addGenericArgumentValue(byKafkaInterface);

        final var beanDef = new GenericBeanDefinition();
        beanDef.setBeanClass(byKafkaInterface);
        beanDef.setConstructorArgumentValues(args);
        beanDef.setFactoryBeanName(ProducerProxyFactory.class.getName());
        beanDef.setFactoryMethodName("newInstance");
        beanDef.setResourceDescription(byKafkaInterface.getCanonicalName());

        return beanDef;
    }
}
