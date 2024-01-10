package me.ehp246.test.embedded.consumer.listener.completed;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfValue;

/**
 * @author Lei Yang
 *
 */
@ByKafka("embedded")
interface Send {
    void send(@OfValue String id);
}
