package me.ehp246.test.embedded.consumer.mdc;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Service;

import me.ehp246.aufkafka.api.annotation.Execution;
import me.ehp246.aufkafka.api.annotation.ForHeader;
import me.ehp246.aufkafka.api.consumer.InstanceScope;

/**
 * @author Lei Yang
 *
 */
@Service
@ForHeader(value = "Ping", execution = @Execution(scope = InstanceScope.BEAN))
public class OnPing {
    private CompletableFuture<Map<String, String>> ref = new CompletableFuture<>();

    void reset() {
        this.ref = new CompletableFuture<>();
    }

    public void apply(final ConsumerRecord<String, String> msg) {
        this.ref.complete(ThreadContext.getContext());
    }

    Map<String, String> take() {
        final Map<String, String> received;
        try {
            received = this.ref.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        this.ref = new CompletableFuture<>();
        return received;
    }
}
