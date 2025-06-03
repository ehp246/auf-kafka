package me.ehp246.aufkafka.api.exception;

import me.ehp246.aufkafka.api.annotation.ByKafka;

/**
 * Indicates that a checked exception happened during the operation of creation
 * the return value for the {@linkplain ByKafka} invocation.
 * <p>
 * The {@linkplain Exception#getCause()} is the checked exception.
 */
public class ProxyReturnBindingException extends RuntimeException {
    private static final long serialVersionUID = -1880647696863110091L;

    public ProxyReturnBindingException(Exception e) {
	super(e);
    }
}
