package me.ehp246.test.embedded.producerfn;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

import me.ehp246.aufkafka.api.consumer.InboundEvent;
import me.ehp246.aufkafka.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
class MsgListener {
    private final AtomicReference<CompletableFuture<InboundEvent>> ref = new AtomicReference<>(
	    new CompletableFuture<InboundEvent>());

    @KafkaListener(topics = AppConfig.TOPIC)
    void onMsg(final ConsumerRecord<String, String> received) {
	ref.get().complete(new InboundEvent(received));
    }

    synchronized InboundEvent take() {
	final var event = OneUtil.orThrow(this.ref.get()::get);
	this.ref.set(new CompletableFuture<>());
	return event;
    }
}
