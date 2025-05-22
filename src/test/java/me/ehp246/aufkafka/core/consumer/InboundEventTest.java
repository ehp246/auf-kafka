package me.ehp246.aufkafka.core.consumer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufkafka.api.consumer.InboundEvent;
import me.ehp246.test.mock.MockConsumerRecord;

class InboundEventTest {

    @Test
    void test_01() {
        Assertions.assertThrows(NullPointerException.class, () -> new InboundEvent(null));
    }

    @Test
    void test_02() {
        final var expected = MockConsumerRecord.withHeaders("h.1", "v.1");
        final InboundEvent event = expected.toEvent();
        final var headerMap = event.headerMap();

        Assertions.assertEquals(expected, event.consumerRecord());
        Assertions.assertThrows(UnsupportedOperationException.class, headerMap::clear, "should not be modifiable");
        Assertions.assertEquals(1, headerMap.size());
        Assertions.assertEquals(1, headerMap.get("h.1").size());
        Assertions.assertEquals("v.1", headerMap.get("h.1").get(0));
        Assertions.assertThrows(UnsupportedOperationException.class, headerMap.get("h.1")::removeFirst,
                "should not be modifiable");
    }
}
