package me.ehp246.aufkafka.core.producer;

import java.util.Map;
import java.util.function.Function;

import org.apache.kafka.clients.producer.Producer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.test.mock.MockProducer;

class DefaultProducerProviderTest {
    private final Function<Map<String, Object>, Producer<String, String>> supplier = map -> new MockProducer();

    @Test
    void test_01() throws Exception {
        new DefaultProducerProvider(supplier, name -> Map.of()).close();
    }

    @Test
    void test_02() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new DefaultProducerProvider(supplier, name -> Map.of()).get(null, null));
    }

    @SuppressWarnings("resource")
    @Test
    void test_03() throws Exception {
        final var provider = new DefaultProducerProvider(supplier, name -> Map.of());

        Assertions.assertEquals(true, provider.get("", null) == provider.get("", null), "should be the same instance");
        Assertions.assertEquals(false, provider.get("", null) == provider.get("1", null),
                "should not be the same instance");
    }

    @Test
    void test_04() throws Exception {
        final var provider = new DefaultProducerProvider(supplier, name -> Map.of());
        final var producer = (MockProducer) provider.get("", null);

        provider.close();

        Assertions.assertEquals(true, producer.isClosed());
    }
}
