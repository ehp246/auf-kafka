package me.ehp246.aufkafka.api;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * @author Lei Yang
 *
 */
public final class AufKafkaConstant {
    public static final String AUFKAFKA_OBJECT_MAPPER = "aufKafkaObjectMapper";
    public static final String BEAN_NAME_PREFIX_INBOUND_ENDPOINT = "inboundEndpoint-";

    public static final String MSG_MDC_HEADER_PREFIX = "AufKafkaMsgMDC";

    /**
     * MDC
     */
    public final static Marker HEADERS = MarkerFactory.getMarker("HEADERS");
    public final static Marker VALUE = MarkerFactory.getMarker("VALUE");
    public final static Marker EXCEPTION = MarkerFactory.getMarker("EXCEPTION");

    private AufKafkaConstant() {
        super();
    }
}
