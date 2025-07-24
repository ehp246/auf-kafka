package me.ehp246.test.embedded.consumer.header.event;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import me.ehp246.aufkafka.api.annotation.Execution;
import me.ehp246.aufkafka.api.annotation.ForEvent;
import me.ehp246.aufkafka.api.consumer.InboundEvent;
import me.ehp246.aufkafka.api.consumer.InstanceScope;
import me.ehp246.aufkafka.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
@ForEvent(value = ".*", execution = @Execution(scope = InstanceScope.BEAN))
public class EventAction {
    private final AtomicReference<CompletableFuture<InboundEvent>> ref = new AtomicReference<>(
            new CompletableFuture<>());

    public synchronized void apply(final ConsumerRecord<String, String> consumerRecord) {
        ref.get().complete(new InboundEvent(consumerRecord));
    }

    public synchronized InboundEvent take() {
        return OneUtil.orThrow(() -> {
            final var received = ref.get().get();
            reset();
            return received;
        });
    }

    public synchronized void reset() {
        ref.set(new CompletableFuture<>());
    }
}