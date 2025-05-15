package me.ehp246.aufkafka.core.configuration;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.util.ClassUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import me.ehp246.aufkafka.api.common.AufKafkaConstant;
import me.ehp246.aufkafka.api.spi.ExpressionResolver;
import me.ehp246.aufkafka.core.provider.jackson.JsonByObjectMapper;

/**
 * Defines the beans that are commonly needed for both consumers and producers.
 * 
 * @author Lei Yang
 * @since 1.0
 */
public final class AufKafkaConfiguration {
    private final static List<String> MODULES = List.of("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule",
            "com.fasterxml.jackson.module.mrbean.MrBeanModule",
            "com.fasterxml.jackson.module.paramnames.ParameterNamesModule");

    @Bean
    ExpressionResolver expressionResolver(final org.springframework.core.env.PropertyResolver springPropertyResolver,
            final ConfigurableBeanFactory beanFactory) {
        final var springSpelResolver = new StandardBeanExpressionResolver();
        final var context = new BeanExpressionContext(beanFactory, null);
        return exp -> {
            if (exp.startsWith("#{")) {
                return springSpelResolver.evaluate(exp, context).toString();
            }
            return springPropertyResolver.resolveRequiredPlaceholders(exp);
        };
    }

    @Bean
    JsonByObjectMapper jsonByObjectMapper(final ApplicationContext appCtx) {
        final var objectMapper = appCtx.getBeansOfType(ObjectMapper.class)
                .get(AufKafkaConstant.BEAN_AUFKAFKA_OBJECT_MAPPER);
        if (objectMapper != null) {
            return new JsonByObjectMapper(objectMapper);
        }

        try {
            return new JsonByObjectMapper(appCtx.getBean(ObjectMapper.class));
        } catch (final Exception e) {
            // Can not find a default. Create private and ignore the exception.
        }

        final ObjectMapper newMapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        MODULES.stream().filter(name -> ClassUtils.isPresent(name, ObjectMapper.class.getClassLoader())).map(name -> {
            try {
                return (Module) Class.forName(name).getDeclaredConstructor((Class[]) null).newInstance((Object[]) null);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException
                    | ClassNotFoundException e) {
                // Ignore failed modules.
                return null;
            }
        }).filter(module -> module != null).forEach(newMapper::registerModule);

        return new JsonByObjectMapper(newMapper);
    }
}
