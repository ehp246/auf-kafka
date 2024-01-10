package me.ehp246.test.embedded.consumer.listener.completed;

import me.ehp246.aufkafka.api.annotation.Applying;
import me.ehp246.aufkafka.api.annotation.ForKey;
import me.ehp246.aufkafka.api.annotation.OfKey;
import me.ehp246.aufkafka.api.annotation.OfValue;

/**
 * @author Lei Yang
 *
 */
@ForKey(".*")
class OnMsg {
    @Applying
    public String perform(@OfKey final String type, @OfValue String value) {
        if (type.equalsIgnoreCase("throw")) {
            throw new RuntimeException();
        }

        return value;
    }
}
