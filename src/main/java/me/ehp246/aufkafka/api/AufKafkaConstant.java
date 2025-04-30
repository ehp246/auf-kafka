package me.ehp246.aufkafka.api;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class AufKafkaConstant {
    public static final String BEAN_NAME_PREFIX_INBOUND_ENDPOINT = "inboundEndpoint-";
    public static final String BEAN_AUFKAFKA_OBJECT_MAPPER = "aufKafkaObjectMapper";
    public static final String BEAN_LOGGING_DISPATCHING_LISTENER = "d8e0d517-5121-43b2-8dc1-4d050fb64086";
    public static final String BEAN_NOOP_UNMATCHED_CONSUMER = "e9c593e2-37c6-48e2-8a76-67540e44e3b1";
    public static final String BEAN_IGNORING_CONSUMEREXCEPTION_LISTENER = "6cf6af85-c802-46c1-97a6-2d5bbfee568a";

    public static final String MSG_MDC_HEADER_PREFIX = "AufKafkaMsgMDC";

    public static final String EVENT_TYPE_HEADER = "AufKafkaEventType";

    public static final String PROPERTY_INBOUND_MESSAGELOGGING_ENABLED = "me.ehp246.aufkafka.inbound.messagelogging.enabled";

    /**
     * MDC
     */
    public final static Marker HEADERS = MarkerFactory.getMarker("HEADERS");
    public final static Marker VALUE = MarkerFactory.getMarker("VALUE");
    public final static Marker EXCEPTION = MarkerFactory.getMarker("EXCEPTION");
    public final static Marker IGNORED = MarkerFactory.getMarker("IGNORED");

    private AufKafkaConstant() {
        super();
    }
}
