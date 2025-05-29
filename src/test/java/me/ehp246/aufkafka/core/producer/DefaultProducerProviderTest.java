package me.ehp246.aufkafka.core.producer;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DefaultProducerProviderTest {
    @Test
    void test_01() throws Exception {
        new DefaultProducerProvider(name -> Map.of()).close();
    }

    @Test
    void test_02() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new DefaultProducerProvider(name -> Map.of()).get(null, null));
    }
}
