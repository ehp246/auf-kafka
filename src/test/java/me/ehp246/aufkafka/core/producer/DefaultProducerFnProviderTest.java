package me.ehp246.aufkafka.core.producer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.apache.kafka.clients.producer.Producer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.ehp246.aufkafka.api.common.AufKafkaConstant;
import me.ehp246.aufkafka.api.producer.ProducerRecordBuilder;
import me.ehp246.test.mock.MockProducer;

class DefaultProducerFnProviderTest {
    private final Function<Map<String, Object>, Producer<String, String>> supplier = map -> new MockProducer();
    private final ProducerRecordBuilder recordBuilder = Mockito.mock(ProducerRecordBuilder.class);

    @Test
    void test_01() throws Exception {
        new DefaultProducerFnProvider(supplier, name -> Map.of(), recordBuilder, n -> null).close();
    }

    @SuppressWarnings("resource")
    @Test
    @Disabled
    void test_03() throws Exception {
        final var provider = new DefaultProducerFnProvider(supplier, name -> Map.of(), recordBuilder, n -> null);

        Assertions.assertEquals(true, provider.get("") == provider.get(""), "should be the same instance");
        Assertions.assertEquals(false, provider.get("") == provider.get("1"), "should not be the same instance");
    }

    @SuppressWarnings("resource")
    @Test
    void test_04() throws Exception {
        final var producer = new MockProducer();
        final var provider = new DefaultProducerFnProvider(map -> producer, name -> Map.of(), recordBuilder, n -> null);

        provider.get("");

        provider.close();

        Assertions.assertEquals(true, producer.isClosed());
    }

    @SuppressWarnings("resource")
    @Test
    void flush_01() {
        final var mockProducer = new MockProducer();
        final var provider = new DefaultProducerFnProvider(map -> mockProducer, name -> Map.of(), recordBuilder,
                n -> null);

        provider.get("").send(""::toString);

        Assertions.assertEquals(false, mockProducer.isFlushed(), "should not have flushed");
    }

    @SuppressWarnings("resource")
    @Test
    void flush_02() {
        final var mockProducer = new MockProducer();
        final var provider = new DefaultProducerFnProvider(map -> mockProducer,
                name -> Map.of(AufKafkaConstant.PRODUCERFN_FLUSH, "true"), recordBuilder, n -> null);

        provider.get("").send(""::toString);

        Assertions.assertEquals(true, mockProducer.isFlushed(), "should have flushed");
    }

    @SuppressWarnings("resource")
    @Test
    void flush_03() {
        final var mockProducer = new MockProducer();
        final var provider = new DefaultProducerFnProvider(map -> mockProducer,
                name -> Map.of(AufKafkaConstant.PRODUCERFN_FLUSH, ""), recordBuilder, n -> null);

        provider.get("").send(""::toString);

        Assertions.assertEquals(false, mockProducer.isFlushed(), "should not have flushed");
    }

    @SuppressWarnings("resource")
    @Test
    void flush_04() {
        final var configMap = new HashMap<String, Object>();
        configMap.put(AufKafkaConstant.PRODUCERFN_FLUSH, null);
        final var mockProducer = new MockProducer();
        final var provider = new DefaultProducerFnProvider(map -> mockProducer, name -> configMap, recordBuilder,
                n -> null);

        provider.get("").send(""::toString);

        Assertions.assertEquals(false, mockProducer.isFlushed(), "should not have flushed");
    }
}
