package me.ehp246.aufkafka.api.consumer;

import me.ehp246.aufkafka.api.AufKafkaConstant;

/**
 * Defines the look-up logic for a {@linkplain EventInvocableRegistry}.
 * 
 * @author Lei Yang
 * @see AufKafkaConstant#HEADER_KEY_EVENT_TYPE
 */
public enum EventInvocableKeyType {
    EVENT_TYPE_HEADER, KEY
}
