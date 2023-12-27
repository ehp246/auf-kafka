package me.ehp246.aufkafka.core.consumer;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionOverrideException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import me.ehp246.aufkafka.api.AufKafkaConstant;
import me.ehp246.aufkafka.api.annotation.EnableForKafka;
import me.ehp246.aufkafka.core.util.OneUtil;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class AnnotatedInboundConsumerRegistrar implements ImportBeanDefinitionRegistrar {
    @SuppressWarnings("unchecked")
    @Override
    public void registerBeanDefinitions(final AnnotationMetadata importingClassMetadata,
            final BeanDefinitionRegistry registry) {
        final var enablerAttributes = importingClassMetadata
                .getAnnotationAttributes(EnableForKafka.class.getCanonicalName());
        if (enablerAttributes == null) {
            return;
        }

        final var defaultConsumer = (String) enablerAttributes.get("defaultConsumer");

        final var inbounds = Arrays
                .asList(((Map<String, Object>[]) enablerAttributes.get("value")));
        for (int i = 0; i < inbounds.size(); i++) {
            final var inbound = inbounds.get(i);
            final var beanDefinition = newBeanDefinition(inbound);

            final Set<String> scanPackages;
            final var base = (Class<?>[]) inbound.get("scan");
            if (base.length > 0) {
                scanPackages = Stream.of(base).map(baseClass -> baseClass.getPackage().getName())
                        .collect(Collectors.toSet());
            } else {
                final var baseName = importingClassMetadata.getClassName();
                scanPackages = Set.of(baseName.substring(0, baseName.lastIndexOf(".")));
            }

            final var beanName = Optional.of(inbound.get("name").toString())
                    .filter(OneUtil::hasValue)
                    .orElse(AufKafkaConstant.BEAN_NAME_PREFIX_INBOUND_ENDPOINT + i);

            final var constructorArgumentValues = new ConstructorArgumentValues();
            constructorArgumentValues.addGenericArgumentValue(inbound);
            constructorArgumentValues.addGenericArgumentValue(scanPackages);
            constructorArgumentValues.addGenericArgumentValue(beanName);
            constructorArgumentValues.addGenericArgumentValue(defaultConsumer);

            beanDefinition.setConstructorArgumentValues(constructorArgumentValues);

            if (registry.containsBeanDefinition(beanName)) {
                throw new BeanDefinitionOverrideException(beanName, beanDefinition,
                        registry.getBeanDefinition(beanName));
            }

            registry.registerBeanDefinition(beanName, beanDefinition);
        }
    }

    private GenericBeanDefinition newBeanDefinition(
            final Map<String, Object> annotationAttributes) {
        final var beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(DefaultInboundConsumer.class);
        beanDefinition.setFactoryBeanName(InboundEndpointFactory.class.getName());
        beanDefinition.setFactoryMethodName("newInstance");

        return beanDefinition;
    }
}
