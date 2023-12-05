package me.ehp246.aufkafka.api.spi;

/**
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface PropertyResolver {
    String resolve(String text);
}
