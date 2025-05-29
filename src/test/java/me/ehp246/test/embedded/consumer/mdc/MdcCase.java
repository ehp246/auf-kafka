package me.ehp246.test.embedded.consumer.mdc;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfHeader;
import me.ehp246.aufkafka.api.annotation.OfKey;
import me.ehp246.aufkafka.api.annotation.OfMdc;
import me.ehp246.aufkafka.api.annotation.OfMdc.Op;
import me.ehp246.aufkafka.api.annotation.OfValue;

/**
 * @author Lei Yang
 *
 */
@ByKafka("embedded")
interface MdcCase {
    void ping(@OfHeader("aufkafka_mdc_OrderId") String aufKafkaEventMDCOrderId);

    void ping(@OfMdc Name name);

    void ping(@OfMdc Name name, @OfHeader @OfMdc String nameProperty);

    @OfKey("Ping")
    void pingWithName(@OfMdc("WithName.") Name name);

    @OfKey("Ping")
    void pingIntroWithName(@OfMdc(value = "WithName.", op = Op.Introspect) Name name);

    @OfKey("Ping")
    void pingIntro(@OfMdc(op = Op.Introspect) Name name);

    void ping2(@OfMdc @OfHeader int accountId, @OfValue Order order);

    void pingOnBody(@OfValue Order order);

    record Name(@OfMdc String firstName, @OfMdc String lastName) {
    }

    record Order(@OfMdc int id, @OfMdc int amount) {
    }
}
