package me.ehp246.test.embedded.consumer.listener.completed;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfKey;
import me.ehp246.aufkafka.api.annotation.OfValue;

/**
 * @author Lei Yang
 *
 */
@ByKafka(value = "embedded", eventTypeHeader = "")
interface Send {
    @OfKey("Send")
    void send(@OfValue String id);
}
