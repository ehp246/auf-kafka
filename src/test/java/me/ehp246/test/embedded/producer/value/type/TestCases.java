package me.ehp246.test.embedded.producer.value.type;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfValue;
import me.ehp246.aufkafka.api.spi.ValueView;
import me.ehp246.test.embedded.producer.value.type.TestCases.Payload.Person;
import me.ehp246.test.embedded.producer.value.type.TestCases.Payload.PersonDob;
import me.ehp246.test.embedded.producer.value.type.TestCases.Payload.PersonName;

/**
 * @author Lei Yang
 *
 */
interface TestCases {
    @ByKafka(value = "embedded")
    interface Case01 {
        void ping(@OfValue Person person);

        void ping(@OfValue PersonName personName);

        void ping(@OfValue PersonDob personDob);
    }

    class Payload extends ValueView {
        interface PersonName {
            @JsonProperty
            String firstName();

            @JsonProperty
            String lastName();
        }

        interface PersonDob {
            @JsonProperty
            Instant dob();
        }

        record Person(String firstName, String lastName, Instant dob)
                implements PersonName, PersonDob {
        }
    }
}
