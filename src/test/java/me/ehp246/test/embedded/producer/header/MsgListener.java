package me.ehp246.test.embedded.producer.header;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

/**
 * @author Lei Yang
 *
 */
class MsgListener {
    private final AtomicReference<CompletableFuture<ConsumerRecord<String, String>>> ref = new AtomicReference<>(
            new CompletableFuture<ConsumerRecord<String, String>>());

    MsgListener reset() {
        this.ref.set(new CompletableFuture<ConsumerRecord<String, String>>());
        return this;
    }

    @KafkaListener(topics = "embedded")
    void onMsg(final ConsumerRecord<String, String> received) {
        ref.get().complete(received);
    }

    ConsumerRecord<String, String> take() throws InterruptedException, ExecutionException {
        return ref.get().get();
    }
}
