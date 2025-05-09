package me.ehp246.test.embedded.consumer.header;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.kafka.common.header.Headers;

import me.ehp246.aufkafka.api.annotation.Execution;
import me.ehp246.aufkafka.api.annotation.ForKey;
import me.ehp246.aufkafka.api.annotation.OfHeader;
import me.ehp246.aufkafka.api.consumer.InstanceScope;

/**
 * @author Lei Yang
 *
 */
@ForKey(value = ".*", execution = @Execution(scope = InstanceScope.BEAN))
public class HeaderAction {
    private final AtomicReference<CompletableFuture<Received>> ref = new AtomicReference<>(new CompletableFuture<>());

    public void apply(@OfHeader final Headers headers, @OfHeader final List<String> headerList,
            @OfHeader final String header1) {
        ref.get().complete(new Received(headers, headerList, header1));
    }

    public Received take() {
        final Received key;

        try {
            key = ref.get().get();
            reset();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return key;
    }

    public void reset() {
        ref.set(new CompletableFuture<Received>());
    }

    record Received(Headers headers, List<String> headerList, String header1) {
    }
}