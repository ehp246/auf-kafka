package me.ehp246.aufkafka.core.producer;

/**
 * @author Lei Yang
 *
 */
public record Pair<K, V>(K key, V value) {
}
