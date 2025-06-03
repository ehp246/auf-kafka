package me.ehp246.aufkafka.core.producer;

import java.util.Map;
import java.util.function.Function;

import org.apache.kafka.clients.producer.Producer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.ehp246.aufkafka.api.producer.ProducerRecordBuilder;
import me.ehp246.test.mock.MockProducer;

class DefaultProducerFnProviderTest {
    private final Function<Map<String, Object>, Producer<String, String>> supplier = map -> new MockProducer();
    private final ProducerRecordBuilder recordBuilder = Mockito.mock(ProducerRecordBuilder.class);

    @Test
    void test_01() throws Exception {
	new DefaultProducerFnProvider(supplier, name -> Map.of(), recordBuilder).close();
    }

    @SuppressWarnings("resource")
    @Test
    @Disabled
    void test_03() throws Exception {
	final var provider = new DefaultProducerFnProvider(supplier, name -> Map.of(), recordBuilder);

	Assertions.assertEquals(true, provider.get("") == provider.get(""), "should be the same instance");
	Assertions.assertEquals(false, provider.get("") == provider.get("1"), "should not be the same instance");
    }

    @SuppressWarnings("resource")
    @Test
    void test_04() throws Exception {
	final var producer = new MockProducer();
	final var provider = new DefaultProducerFnProvider(map -> producer, name -> Map.of(), recordBuilder);

	provider.get("");

	provider.close();

	Assertions.assertEquals(true, producer.isClosed());
    }

    @SuppressWarnings("resource")
    @Test
    void flush_01() throws Exception {
	final var producer = new MockProducer();

	new DefaultProducerFnProvider(map -> producer, name -> Map.of(), recordBuilder).get("").send(""::toString);

	Assertions.assertEquals(false, producer.isFlushed());
    }

    @SuppressWarnings("resource")
    @Test
    void flush_02() throws Exception {
	final var producer = new MockProducer();

	new DefaultProducerFnProvider(map -> producer, name -> Map.of(), recordBuilder)
		.get("", Boolean.FALSE::booleanValue).send(""::toString);

	Assertions.assertEquals(false, producer.isFlushed());
    }

    @SuppressWarnings({ "resource", "unchecked" })
    @Test
    void flush_03() throws Exception {
	final var producer = Mockito.mock(Producer.class);
	final var count = new int[] { 0 };

	final var producerFn = new DefaultProducerFnProvider(map -> producer, name -> Map.of(), recordBuilder).get("",
		() -> {
		    count[0]++;
		    return true;
		});

	producerFn.send(""::toString);

	Mockito.verify(producer).flush();
	Assertions.assertEquals(1, count[0]);

	producerFn.send(""::toString);

	Mockito.verify(producer, Mockito.times(2)).flush();
	Assertions.assertEquals(2, count[0], "should be called for each invocation");
    }
}
