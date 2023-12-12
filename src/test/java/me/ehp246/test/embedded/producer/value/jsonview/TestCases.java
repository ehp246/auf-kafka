package me.ehp246.test.embedded.producer.value.jsonview;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufkafka.api.annotation.ByKafka;
import me.ehp246.aufkafka.api.annotation.OfValue;
import me.ehp246.aufkafka.api.spi.ValueView;
import me.ehp246.test.embedded.producer.value.jsonview.TestCases.Payload.AccountRequest;

/**
 * @author Lei Yang
 *
 */
interface TestCases {
    @ByKafka(value = "embedded")
    interface Case01 {
        void withAll(@OfValue @JsonView(Payload.class) AccountRequest value);

        void withoutPassword(@OfValue @JsonView(ValueView.class) AccountRequest value);
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

        record AccountRequest(@JsonView(ValueView.class) String id,
                @JsonView(Payload.class) String password) {
        }
    }
}
