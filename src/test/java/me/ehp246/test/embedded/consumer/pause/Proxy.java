package me.ehp246.test.embedded.consumer.pause;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfHeader;
import me.ehp246.aufkafka.api.annotation.OfValue;
import me.ehp246.aufkafka.api.common.AufKafkaConstant;

@ByKafka(App.TOPIC)
interface Proxy {
    void pause(@OfHeader(AufKafkaConstant.CORRELATIONID_HEADER) String id, @OfValue int i);
}
