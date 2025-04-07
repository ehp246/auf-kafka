package me.ehp246.aufkafka.core.consumer.case01;

import me.ehp246.aufkafka.api.annotation.ForKey;

@ForKey("test")
class NonPublic {
    public void invoke() {
    }
}
