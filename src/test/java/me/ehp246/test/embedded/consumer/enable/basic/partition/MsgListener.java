package me.ehp246.test.embedded.consumer.enable.basic.partition;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;

import me.ehp246.aufkafka.api.consumer.InboundEvent;
import me.ehp246.aufkafka.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
class MsgListener {
    private final AtomicReference<CompletableFuture<InboundEvent>> ref = new AtomicReference<>(
            new CompletableFuture<InboundEvent>());

    @KafkaListener(topicPartitions = @TopicPartition(topic = AppConfig.TOPIC, partitions = "4"), groupId = "59ed3099-0f0d-40be-90af-5ac9e2ddf517")
    void onMsg(final ConsumerRecord<String, String> received) {
        if (ref.get().isDone()) {
            throw new RuntimeException("Has been completed");
        }
        ref.get().complete(new InboundEvent(received));
    }

    InboundEvent take() {
        final var value = OneUtil.orThrow(() -> ref.get().get());
        reset();
        return value;
    }

    void reset() {
        ref.set(new CompletableFuture<InboundEvent>());
    }
}
