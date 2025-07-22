package me.ehp246.aufkafka.core.consumer;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.record.TimestampType;

import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufkafka.api.annotation.OfHeader;
import me.ehp246.aufkafka.api.annotation.OfKey;
import me.ehp246.aufkafka.api.annotation.OfMdc;
import me.ehp246.aufkafka.api.annotation.OfMdc.Op;
import me.ehp246.aufkafka.api.annotation.OfOffset;
import me.ehp246.aufkafka.api.annotation.OfPartition;
import me.ehp246.aufkafka.api.annotation.OfTimestamp;
import me.ehp246.aufkafka.api.annotation.OfTopic;
import me.ehp246.aufkafka.api.annotation.OfValue;
import me.ehp246.aufkafka.api.consumer.InboundEvent;
import me.ehp246.aufkafka.api.consumer.InboundEventContext;
import me.ehp246.aufkafka.api.serializer.jackson.FromJson;
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

    /**
     * Type-based injection
     */
    static class TypeCase01 {
        public void m01() {
        }

        public void m01(final String value) {
        }

        public void m01(final InboundEvent event) {
        }

        public Consumer<String, String> m01(final Consumer<String, String> consumer) {
            return consumer;
        }

        public ConsumerRecord<String, String> m01(final ConsumerRecord<String, String> msg) {
            return msg;
        }

        public ConsumerRecord<String, String> m01(final ConsumerRecord<String, String> msg, final FromJson fromJson) {
            return msg;
        }

        public Object[] m01(@OfValue final List<Integer> integers, final ConsumerRecord<String, String> msg) {
            return new Object[] { integers, msg };
        }

        public Object[] header(final Headers headers, final Header myHeader) {
            return new Object[] { headers, myHeader };
        }

        public Object[] typeArg(InboundEventContext context) {
            return new Object[] { context };
        }
    }

    static class TopicCase01 {
        public Object[] topic(@OfTopic final String topic) {
            return new Object[] { topic };
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

    static class ParititionCase01 {
        public Object[] m01(final InboundEvent event, @OfPartition int partition) {
            return new Object[] { event, partition };
        }

        public Object[] m01(@OfPartition Integer partition) {
            return new Object[] { partition };
        }

        public Object[] m01(@OfPartition Number partition) {
            return new Object[] { partition };
        }
    }

    static class OffsetCase01 {
        public Object[] m01(@OfOffset final long offset) {
            return new Object[] { offset };
        }

        public Object[] m01(@OfOffset Long offset) {
            return new Object[] { offset };
        }

    }

    static class TimestampCase01 {
        public Object[] m01(@OfTimestamp long timestamp) {
            return new Object[] { timestamp };
        }

        public Object[] m01(@OfTimestamp Long timestamp) {
            return new Object[] { timestamp };
        }

        public Object[] m01(@OfTimestamp Instant timestamp) {
            return new Object[] { timestamp };
        }

        public Object[] m01(final TimestampType timestampType) {
            return new Object[] { timestampType };
        }
    }

    static class HeaderCase01 {
        public String m01(@OfHeader("header1") final String value) {
            return value;
        }

        public String[] m01(@OfHeader("prop1") final String value1, @OfHeader("prop2") final String value2) {
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

        public Object[] iterableList(@OfHeader final Iterable<Header> iterable, @OfHeader() final List<String> list) {
            return new Object[] { iterable, list };
        }

        enum PropertyEnum {
            Enum1
        }
    }

    static class MdcCase {
        public void get() {
        }

        public void get(@OfMdc("name") @OfHeader final String firstName,
                @OfMdc("name") @OfHeader final String lastName) {
        }

        public void get(@OfMdc @OfValue final String name, @OfMdc("SSN") @OfHeader final int id) {
        }

        public void get(@OfMdc @OfValue final String name, @OfMdc("SSN") @OfHeader final Integer id) {
        }

        public void getOnBody(@OfMdc @OfValue final Name name) {
        }

        public void getOnBodyPrec(@OfMdc(op = Op.Introspect) @OfValue final Name name,
                @OfMdc("firstName") @OfHeader("firstName") final String nameProperty) {
        }

        public void getOnBodyNamed(@OfMdc("newName") @OfValue final Name name,
                @OfHeader @OfMdc final String firstName) {
        }

        public void getOnBodyIntro(@OfMdc(op = Op.Introspect) @OfValue Name name) {
        }

        public void getOnBodyIntroNamed(@OfMdc(value = "Name.", op = Op.Introspect) @OfValue final Name name) {
        }

        public void getInBody(@OfValue final Name name) {
        }

        record Name(@OfMdc String firstName, @OfMdc String lastName) {
            @OfMdc
            String fullName() {
                return firstName + lastName;
            }

            @OfMdc(op = Op.Introspect)
            String middleName() {
                return null;
            }
        }
    }

    static class PerfCase {
        public Object[] m01(@OfKey final String type, @OfHeader final String id, @OfHeader("prop1") final String prop1,
                final Integer body, final ConsumerRecord<String, String> msg, final FromJson fromJson) {
            return new Object[] { type, id, prop1, body };
        }

    }
}