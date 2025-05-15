package me.ehp246.test.embedded.consumer.header.injection;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.kafka.common.header.Headers;

import me.ehp246.aufkafka.api.annotation.Execution;
import me.ehp246.aufkafka.api.annotation.ForKey;
import me.ehp246.aufkafka.api.annotation.OfHeader;
import me.ehp246.aufkafka.api.consumer.InstanceScope;
import me.ehp246.aufkafka.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
@ForKey(value = ".*", execution = @Execution(scope = InstanceScope.BEAN))
public class HeaderInjectAction {
    private final AtomicReference<CompletableFuture<Received>> ref = new AtomicReference<>(new CompletableFuture<>());

    public void apply(@OfHeader final Headers headers, @OfHeader final List<String> headerList,
            @OfHeader final String header1) {
        ref.get().complete(new Received(headers, headerList, header1));
    }

    public Received take() {
        return OneUtil.orThrow(() -> {
            final var received = ref.get().get();
            reset();
            return received;
        });
    }

    public synchronized void reset() {
        ref.set(new CompletableFuture<Received>());
    }

    record Received(Headers headers, List<String> headerList, String header1) {
    }
}