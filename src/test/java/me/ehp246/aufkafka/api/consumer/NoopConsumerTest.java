package me.ehp246.aufkafka.api.consumer;

import org.junit.jupiter.api.Test;

import me.ehp246.test.mock.MockConsumerRecord;

/**
 * @author Lei Yang
 *
 */
class NoopConsumerTest {

    @Test
    void test() {
        new NoOpConsumer().onUnmatched(new MockConsumerRecord());
    }

}
