package me.ehp246.aufkafka.core.consumer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import me.ehp246.aufkafka.api.consumer.InvocableScanner;

class InboundEndpointFactoryTest {
	private final AutowireCapableBeanFactory beanFactory = new DefaultListableBeanFactory();
	private final InvocableScanner is = (r, s) -> Set.of();

	@Test
	void properties_01() {
	}

}
