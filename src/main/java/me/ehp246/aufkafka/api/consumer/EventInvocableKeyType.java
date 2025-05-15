package me.ehp246.aufkafka.api.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import me.ehp246.aufkafka.api.common.AufKafkaConstant;

/**
 * Defines the look-up logic for a {@linkplain EventInvocableRegistry}.
 * 
 * @author Lei Yang
 * @see AufKafkaConstant#EVENT_HEADER
 */
public enum EventInvocableKeyType {
    /**
     * Indicates to use the event header value for the name.
     */
    EVENT_HEADER,
    /**
     * Indicates to use {@linkplain ConsumerRecord#key()} for the name.
     */
    KEY
}
