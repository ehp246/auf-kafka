package me.ehp246.test.mock;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;

/**
 * @author Lei Yang
 *
 */
public class StringHeader implements Header {
    private final String key;
    private final String value;
    private final byte[] bytes;

    public StringHeader(final String key, final String value) {
	super();
	this.key = Objects.requireNonNull(key);
	this.value = value;
	this.bytes = value == null ? null : value.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] value() {
	return this.bytes;
    }

    /**
     * @param headers Must be even length.
     * @return
     */
    public static Headers headers(final String... headers) {
	if (headers.length == 0) {
	    return new RecordHeaders();
	}

	final var list = new ArrayList<Header>(headers.length / 2);
	for (int i = 0; i < headers.length; i++) {
	    list.add(new StringHeader(headers[i], headers[++i]));
	}

	return new RecordHeaders(list);
    }

    public static Headers headers(final Map<String, String> map) {
	final var array = new String[map.entrySet().size() * 2];
	int i = 0;
	for (var entry : map.entrySet()) {
	    array[i] = entry.getKey();
	    array[i + 1] = entry.getValue();
	    i += 2;
	}

	return headers(array);
    }

    @Override
    public String key() {
	return this.key;
    }
}
