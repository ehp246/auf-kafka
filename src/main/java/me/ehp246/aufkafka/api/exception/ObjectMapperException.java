package me.ehp246.aufkafka.api.exception;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Runtime exception for {@linkplain ObjectMapper}.
 * 
 * @author Lei Yang
 *
 */
public final class ObjectMapperException extends RuntimeException {
    private static final long serialVersionUID = 2456908103414256747L;

    public ObjectMapperException(final JacksonException cause) {
        super(cause);
    }
}
