package me.ehp246.aufkafka.core.consumer;

import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;

import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufkafka.api.annotation.OfHeader;
import me.ehp246.aufkafka.api.annotation.OfKey;
import me.ehp246.aufkafka.api.annotation.OfMDC;
import me.ehp246.aufkafka.api.annotation.OfMDC.Op;
import me.ehp246.aufkafka.api.annotation.OfValue;
import me.ehp246.aufkafka.api.serializer.json.FromJson;
import me.ehp246.aufkafka.api.spi.ValueView;

interface InvocableBinderTestCases {
    static class ValueCase01 {
        record Account(String id, String password) {
        }

        public interface Received {
            @JsonView({ ValueView.class, String.class })
            String getId();

            @JsonView({ String.class })
            String getPassword();
        }

        public Received m01(@OfValue final Received payload) {
            return payload;
        }

        public Received m02(@OfValue @JsonView(ValueView.class) final Received payload) {
            return payload;
        }

        public Received m03(@OfValue @JsonView(String.class) final Received payload) {
            return payload;
        }

        public Received m04(@OfValue @JsonView(Integer.class) final Received payload) {
            return payload;
        }
    }

    static class ArgCase01 {
        public void m01() {
        }

        public void m01(final String value) {
        }

        public ConsumerRecord<String, String> m01(final ConsumerRecord<String, String> msg) {
            return msg;
        }

        public ConsumerRecord<String, String> m01(final ConsumerRecord<String, String> msg,
                final FromJson fromJson) {
            return msg;
        }

        public Object[] m01(@OfValue final List<Integer> integers,
                final ConsumerRecord<String, String> msg) {
            return new Object[] { integers, msg };
        }
    }

    static class ExceptionCase01 {
        public void m01() {
            throw new IllegalArgumentException();
        }
    }

    static class KeyCase01 {
        public Object[] m01(final ConsumerRecord<String, String> msg, @OfKey final String key,
                @OfValue final String payload) {
            return new Object[] { msg, key, payload };
        }
    }

    static class HeaderCase01 {
        public String m01(@OfHeader("header1") final String value) {
            return value;
        }

        public String[] m01(@OfHeader("prop1") final String value1,
                @OfHeader("prop2") final String value2) {
            return new String[] { value1, value2 };
        }

        public Object[] m01(@OfHeader final Map<String, List<String>> value1) {
            return new Object[] { value1 };
        }

        public Object[] m01(@OfHeader final Headers value1) {
            return new Object[] { value1 };
        }

        public Boolean m01(@OfHeader final Boolean prop1) {
            return prop1;
        }

        public PropertyEnum m01(@OfHeader("prop1") final PropertyEnum value) {
            return value;
        }

        enum PropertyEnum {
            Enum1
        }
    }

    static class MDCCase {
        public void get() {
        }

        public void get(@OfMDC("name") @OfHeader final String firstName,
                @OfMDC("name") @OfHeader final String lastName) {
        }

        public void get(@OfMDC @OfValue final String name,
                @OfMDC("SSN") @OfHeader final int id) {
        }

        public void get(@OfMDC @OfValue final String name,
                @OfMDC("SSN") @OfHeader final Integer id) {
        }

        public void getOnBody(@OfMDC @OfValue final Name name) {
        }

        public void getOnBodyPrec(@OfMDC(op = Op.Introspect) @OfValue final Name name,
                @OfMDC("firstName") @OfHeader("firstName") final String nameProperty) {
        }

        public void getOnBodyNamed(@OfMDC("newName") @OfValue final Name name,
                @OfHeader @OfMDC final String firstName) {
        }

        public void getOnBodyIntro(@OfMDC(op = Op.Introspect) @OfValue Name name) {
        }

        public void getOnBodyIntroNamed(
                @OfMDC(value = "Name.", op = Op.Introspect) @OfValue final Name name) {
        }

        public void getInBody(@OfValue final Name name) {
        }

        record Name(@OfMDC String firstName, @OfMDC String lastName) {
            @OfMDC
            String fullName() {
                return firstName + lastName;
            }

            @OfMDC(op = Op.Introspect)
            String middleName() {
                return null;
            }
        }
    }
}