package me.ehp246.aufkafka.api;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * @author Lei Yang
 *
 */
public final class AufKafkaConstant {
    public static final String BEAN_AUFKAFKA_OBJECT_MAPPER = "aufKafkaObjectMapper";
    public static final String BEAN_NAME_PREFIX_INBOUND_ENDPOINT = "inboundEndpoint-";

    public static final String BEAN_LOGING_CONSUMER = "d8e0d517-5121-43b2-8dc1-4d050fb64086";

    public static final String MSG_MDC_HEADER_PREFIX = "AufKafkaMsgMDC";

    public static final String PROPERTY_CONSUMER_MESSAGE_LOGGING = "me.ehp246.aufkafka.consumer.messagelogging.enabled";

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
