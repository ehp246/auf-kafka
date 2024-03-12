package me.ehp246.aufkafka.core.producer;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufkafka.api.producer.ProducerFnProvider.ProducerFnConfig;

class ProducerProxyFactoryTest {

	@Test
	void properties_01() {
		final var ref = new ProducerFnConfig[1];
		final var factory = new ProducerProxyFactory(new DefaultProxyMethodParser(Object::toString), c -> {
			ref[0] = c;
			return null;
		}, new MockEnvironment()::resolveRequiredPlaceholders);

		factory.newInstance(ProducerProxyFactoryTestCases.Case01.class);

		Assertions.assertEquals(0, ref[0].producerProperties().size());
	}

	@Test
	void properties_02() {
		final var v2 = UUID.randomUUID().toString();
		final var ref = new ProducerFnConfig[1];

		final var factory = new ProducerProxyFactory(new DefaultProxyMethodParser(Object::toString), c -> {
			ref[0] = c;
			return null;
		}, new MockEnvironment().withProperty("v2", v2)::resolveRequiredPlaceholders);

		factory.newInstance(ProducerProxyFactoryTestCases.Case02.class);

		final var producerProperties = ref[0].producerProperties();

		Assertions.assertEquals(2, producerProperties.size());
		Assertions.assertEquals("v1", producerProperties.get("p1"));
		Assertions.assertEquals(v2, producerProperties.get("p2"));
	}
}
