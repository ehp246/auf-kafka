package me.ehp246.aufkafka.api;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * @author Lei Yang
 *
 */
public final class AufKafkaConstant {
    public static final String AUFKAFKA_OBJECT_MAPPER = "aufKafkaObjectMapper";
    public static final String BEAN_NAME_PREFIX_INBOUND_ENDPOINT = "InboundEndpoint-";

    public static final String LOG4J_CONTEXT_HEADER_PREFIX = "AufKafkaLog4jContext";

    /**
     * Log4J
     */
    public final static Marker HEADERS = MarkerManager.getMarker("HEADERS");
    public final static Marker VALUE = MarkerManager.getMarker("VALUE");
    public final static Marker EXCEPTION = MarkerManager.getMarker("EXCEPTION");

    private AufKafkaConstant() {
        super();
    }
}
