package me.ehp246.aufkafka.api.exception;

public class ProxyReturnBindingException extends RuntimeException {
    private static final long serialVersionUID = -1880647696863110091L;

    public ProxyReturnBindingException(Exception e) {
	super(e);
    }
}
