package me.ehp246.aufkafka.api.consumer;

import me.ehp246.aufkafka.api.AufKafkaConstant;

/**
 * Defines the look-up logic for a {@linkplain EventInvocableRegistry}.
 * 
 * @author Lei Yang
 * @see AufKafkaConstant#EVENT_HEADER
 */
public enum EventInvocableKeyType {
    EVENT_HEADER, KEY
}
