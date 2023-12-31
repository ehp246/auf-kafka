package me.ehp246.aufkafka.core.consumer;

import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.protocol.Message;

import me.ehp246.aufkafka.api.annotation.OfCorrelationId;
import me.ehp246.aufkafka.api.annotation.OfHeader;
import me.ehp246.aufkafka.api.annotation.OfKey;
import me.ehp246.aufkafka.api.annotation.OfLog4jContext;
import me.ehp246.aufkafka.api.annotation.OfLog4jContext.Op;
import me.ehp246.aufkafka.api.serializer.json.FromJson;

interface InvocableBinderTestCases {
    static class ArgCase01 {
        public void m01() {

        }

        public ConsumerRecord<String, String> m01(final ConsumerRecord<String, String> msg) {
            return msg;
        }

        public ConsumerRecord<String, String> m01(final ConsumerRecord<String, String> msg,
                final FromJson fromJson) {
            return msg;
        }

        public Object[] m01(final List<Integer> integers,
                final ConsumerRecord<String, String> msg) {
            return new Object[] { integers, msg };
        }
    }

    static class MethodCase01 {
        public void m01() {

        }

        public ConsumerRecord<String, String> m01(final ConsumerRecord<String, String> msg) {
            return msg;
        }

        public Object[] m01(final ConsumerRecord<String, String> msg, final FromJson fromJson) {
            return new Object[] { msg, fromJson };
        }

        public Object[] m01(final List<Integer> integers, final Message message) {
            return new Object[] { integers, message };
        }

        public Void m02() {
            return null;
        }

    }

    static class ExceptionCase01 {
        public void m01() {
            throw new IllegalArgumentException();
        }
    }

    static class KeyCase01 {
        public Object[] m01(final ConsumerRecord<String, String> msg, @OfKey final String type,
                final String payload) {
            return new Object[] { msg, type, payload };
        }
    }

    static class PartitionCase01 {
        public Object[] m01(final Integer partition) {
            return new Object[] { partition };
        }
    }

    static class CorrelationIdCase01 {
        private String field;
        public String setter;
        public String method;

        public String get() {
            return this.field;
        }

        public void set(final String id) {
            setter = id;
        }

        public String[] m01(@OfCorrelationId final String id1, @OfCorrelationId final String id2) {
            return new String[] { id1, id2 };
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

    static class Log4jContextCase {
        public void get() {
        }

        public void get(@OfLog4jContext("name") @OfHeader final String firstName,
                @OfLog4jContext("name") @OfHeader final String lastName) {
        }

        public void get(@OfLog4jContext final String name,
                @OfLog4jContext("SSN") @OfHeader final int id) {
        }

        public void get(@OfLog4jContext final String name,
                @OfLog4jContext("SSN") @OfHeader final Integer id) {
        }

        public void getOnBody(@OfLog4jContext final Name name) {
        }

        public void getOnBodyPrec(@OfLog4jContext(op = Op.Introspect) final Name name,
                @OfLog4jContext("firstName") @OfHeader("firstName") final String nameProperty) {
        }

        public void getOnBodyNamed(@OfLog4jContext("newName") final Name name,
                @OfHeader @OfLog4jContext final String firstName) {
        }

        public void getOnBodyIntro(@OfLog4jContext(op = Op.Introspect) final Name name) {
        }

        public void getOnBodyIntroNamed(
                @OfLog4jContext(value = "Name.", op = Op.Introspect) final Name name) {
        }

        public void getInBody(final Name name) {
        }

        record Name(@OfLog4jContext String firstName, @OfLog4jContext String lastName) {
            @OfLog4jContext
            String fullName() {
                return firstName + lastName;
            }

            @OfLog4jContext(op = Op.Introspect)
            String middleName() {
                return null;
            }
        }
    }
}