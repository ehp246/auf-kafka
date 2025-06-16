package me.ehp246.aufkafka.core.consumer;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import me.ehp246.aufkafka.api.consumer.DispatchListener;
import me.ehp246.aufkafka.api.consumer.EventInvocableRunnableBuilder;
import me.ehp246.aufkafka.api.consumer.InboundEvent;
import me.ehp246.aufkafka.api.consumer.InvocableFactory;

/**
 * @author Lei Yang
 *
 */
class DefaultInboundEndpointConsumerTest {
    record Context(InboundEvent event, Exception thrown) {
    }

    private final EventInvocableRunnableBuilder dispatcher = (i, r) -> () -> {
    };
    private final InvocableFactory factory = r -> null;

    private final TopicPartition partition = new TopicPartition("", 0);

    @Test
    void exception_01() throws InterruptedException, ExecutionException {
        final var ref = new CompletableFuture<Context>();
        final var msg = new ConsumerRecord<String, String>("", 0, 0, null, null);
        final var consumer = new MockConsumer<String, String>(OffsetResetStrategy.EARLIEST);
        consumer.assign(List.of(partition));
        consumer.updateBeginningOffsets(Map.of(partition, 0L));

        consumer.addRecord(msg);

        final var thrown = new RuntimeException();

        final var task = new DefaultInboundEndpointConsumer(consumer, Duration.ofDays(1)::abs, dispatcher,
                (InvocableFactory) (r -> {
                    throw thrown;
                }), null, null, (DispatchListener.ExceptionListener) (e, t) -> ref.complete(new Context(e, t)));

        Executors.newVirtualThreadPerTaskExecutor().execute(task::run);

        final var context = ref.get();

        Assertions.assertEquals(thrown, context.thrown());
        Assertions.assertEquals(msg, context.event().consumerRecord());
        Assertions.assertEquals(1, consumer.committed(Set.of(partition)).get(partition).offset());
    }

    @Test
    void pollDuration_01() throws InterruptedException, ExecutionException {
        final var expected = Duration.ofDays(2);

        final var consumer = new MockConsumer<String, String>(OffsetResetStrategy.EARLIEST);
        consumer.assign(List.of(partition));
        consumer.updateBeginningOffsets(Map.of(partition, 0L));

        final var spyConsumer = Mockito.spy(consumer);

        final var task = new DefaultInboundEndpointConsumer(spyConsumer, () -> expected, dispatcher, factory, null,
                null, null);

        final var ref = new CompletableFuture<Exception>();
        Executors.newVirtualThreadPerTaskExecutor().execute(() -> {
            task.run();
            ref.complete(null);
        });

        Executors.newVirtualThreadPerTaskExecutor().execute(() -> {
            task.close();
            ref.complete(null);
        });
        ref.get();

        ArgumentCaptor<Duration> argument = ArgumentCaptor.forClass(Duration.class);

        Mockito.verify(spyConsumer, Mockito.atLeastOnce()).poll(argument.capture());

        Assertions.assertEquals(expected, argument.getValue());
    }
}
