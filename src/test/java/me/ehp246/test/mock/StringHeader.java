package me.ehp246.test.mock;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;

/**
 * @author Lei Yang
 *
 */
public record StringHeader(String key, String strValue) implements Header {
    @Override
    public byte[] value() {
        return this.strValue.getBytes(StandardCharsets.UTF_8);
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
            list.add(new StringHeader(headers[i], headers[i++]));
        }

        return new RecordHeaders(list);
    }

    public static Headers headers(final Map<String, String> map) {
        final var array = new String[map.entrySet().size() * 2];
        int i = 0;
        for (var entry : map.entrySet()) {
            array[i] = entry.getKey();
            array[i++] = entry.getValue();
        }

        return headers(array);
    }
}
