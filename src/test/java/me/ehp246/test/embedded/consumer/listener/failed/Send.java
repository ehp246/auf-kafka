package me.ehp246.test.embedded.consumer.listener.failed;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfKey;
import me.ehp246.aufkafka.api.annotation.OfValue;

/**
 * @author Lei Yang
 *
 */
@ByKafka(value = "embedded", methodAsHeader = "")
interface Send {
    @OfKey("FailedMsg")
    void failedMsg(@OfValue String id);
}
