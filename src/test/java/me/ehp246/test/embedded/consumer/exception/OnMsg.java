package me.ehp246.test.embedded.consumer.exception;

import me.ehp246.aufkafka.api.annotation.ForKey;
import me.ehp246.aufkafka.api.annotation.OfKey;

/**
 * @author Lei Yang
 *
 */
@ForKey(".*")
class OnMsg {

    public void apply(@OfKey final String key) {
        throw new IllegalArgumentException(key);
    }
}
