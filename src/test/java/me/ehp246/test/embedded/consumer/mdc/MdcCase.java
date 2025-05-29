package me.ehp246.test.embedded.consumer.mdc;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfHeader;
import me.ehp246.aufkafka.api.annotation.OfKey;
import me.ehp246.aufkafka.api.annotation.OfMDC;
import me.ehp246.aufkafka.api.annotation.OfMDC.Op;
import me.ehp246.aufkafka.api.annotation.OfValue;

/**
 * @author Lei Yang
 *
 */
@ByKafka("embedded")
interface MdcCase {
    void ping(@OfHeader("aufkafka_mdc_OrderId") String aufKafkaEventMDCOrderId);

    void ping(@OfMDC Name name);

    void ping(@OfMDC Name name, @OfHeader @OfMDC String nameProperty);

    @OfKey("Ping")
    void pingWithName(@OfMDC("WithName.") Name name);

    @OfKey("Ping")
    void pingIntroWithName(@OfMDC(value = "WithName.", op = Op.Introspect) Name name);

    @OfKey("Ping")
    void pingIntro(@OfMDC(op = Op.Introspect) Name name);

    void ping2(@OfMDC @OfHeader int accountId, @OfValue Order order);

    void pingOnBody(@OfValue Order order);

    record Name(@OfMDC String firstName, @OfMDC String lastName) {
    }

    record Order(@OfMDC int id, @OfMDC int amount) {
    }
}
