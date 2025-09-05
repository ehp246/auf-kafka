package me.ehp246.test.embedded.consumer.defaultconsumer.unmatched;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfKey;

/**
 * @author Lei Yang
 *
 */
@ByKafka(AppConfig.TOPIC)
interface Send {
    void send(@OfKey String key);
}
