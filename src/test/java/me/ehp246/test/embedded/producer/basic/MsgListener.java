package me.ehp246.test.embedded.producer.basic;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

import me.ehp246.aufkafka.core.consumer.InboundEvent;
import me.ehp246.aufkafka.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
class MsgListener {
    private final AtomicReference<CompletableFuture<InboundEvent>> ref = new AtomicReference<>(
            new CompletableFuture<InboundEvent>());

    MsgListener reset() {
        this.ref.set(new CompletableFuture<>());
        return this;
    }

    @KafkaListener(topics = "embedded")
    void onMsg(final ConsumerRecord<String, String> received) {
        ref.get().complete(new InboundEvent(received));
    }

    InboundEvent take() {
        return OneUtil.orThrow(this.ref.get()::get);
    }
}
