package me.ehp246.test.mock;

import java.util.Map;

import me.ehp246.aufkafka.api.consumer.ConsumerConfigProvider;

/**
 * @author Lei Yang
 *
 */
public class MockConsumerConfigProvider implements ConsumerConfigProvider {

    @Override
    public Map<String, Object> get(String name) {
        return null;
    }

}
