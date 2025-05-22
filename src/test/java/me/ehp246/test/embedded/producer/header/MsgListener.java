package me.ehp246.test.embedded.producer.header;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

import me.ehp246.aufkafka.api.consumer.InboundEvent;
import me.ehp246.aufkafka.api.consumer.InboundEvent;
import me.ehp246.aufkafka.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
class MsgListener {
    private final AtomicReference<CompletableFuture<ConsumerRecord<String, String>>> ref = new AtomicReference<>(
            new CompletableFuture<ConsumerRecord<String, String>>());

    private final AtomicReference<CompletableFuture<InboundEvent>> refInboud = new AtomicReference<>(
            new CompletableFuture<InboundEvent>());

    MsgListener reset() {
        this.ref.set(new CompletableFuture<ConsumerRecord<String, String>>());
        this.refInboud.set(new CompletableFuture<InboundEvent>());
        return this;
    }

    @KafkaListener(topics = "embedded")
    void onMsg(final ConsumerRecord<String, String> received) {
        ref.get().complete(received);
        refInboud.get().complete(new InboundEvent(received));
    }

    ConsumerRecord<String, String> take() throws InterruptedException, ExecutionException {
        return ref.get().get();
    }

    InboundEvent takeInboud() {
        return OneUtil.orThrow(() -> refInboud.get().get());
    }
}
