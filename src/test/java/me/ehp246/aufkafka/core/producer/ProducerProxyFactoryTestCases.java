package me.ehp246.aufkafka.core.producer;

import me.ehp246.aufkafka.api.annotation.ByKafka;

class ProducerProxyFactoryTestCases {
	@ByKafka("")
	interface Case01 {
	}

	@ByKafka(value = "", producerProperties = { "p1", "v1", "p2", "${v2}" })
	interface Case02 {
	}
}
