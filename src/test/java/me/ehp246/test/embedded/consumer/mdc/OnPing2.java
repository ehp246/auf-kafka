package me.ehp246.test.embedded.consumer.mdc;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Service;

import me.ehp246.aufkafka.api.annotation.Execution;
import me.ehp246.aufkafka.api.annotation.ForKey;
import me.ehp246.aufkafka.api.annotation.OfHeader;
import me.ehp246.aufkafka.api.annotation.OfMDC;
import me.ehp246.aufkafka.api.annotation.OfValue;
import me.ehp246.aufkafka.api.consumer.InstanceScope;
import me.ehp246.aufkafka.api.consumer.InvocationModel;

/**
 * @author Lei Yang
 *
 */
@Service
@ForKey(value = "Ping2", execution = @Execution(scope = InstanceScope.BEAN, invocation = InvocationModel.DEFAULT))
public class OnPing2 {
    private final AtomicReference<CompletableFuture<Map<String, String>>> ref = new AtomicReference<>(
            new CompletableFuture<>());

    public void apply(@OfValue final Order order, @OfMDC @OfHeader final int accountId) {
        this.ref.get().complete(ThreadContext.getContext());
    }

    Map<String, String> take() {
        final Map<String, String> received;
        try {
            received = this.ref.get().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        this.ref.set(new CompletableFuture<>());
        return received;
    }

    public record Order(@OfMDC("OrderId") int id, @OfMDC int amount) {
    }
}
