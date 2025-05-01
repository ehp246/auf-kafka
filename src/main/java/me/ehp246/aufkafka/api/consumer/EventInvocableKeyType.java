package me.ehp246.aufkafka.api.consumer;

import me.ehp246.aufkafka.api.ReservedHeader;

/**
 * Defines the look-up logic for a {@linkplain EventInvocableRegistry}.
 * 
 * @author Lei Yang
 * @see ReservedHeader#AufKafkaEventType
 */
public enum EventInvocableKeyType {
    EVENT_TYPE_HEADER, KEY
}
