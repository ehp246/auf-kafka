package me.ehp246.aufkafka.core.consumer;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufkafka.api.consumer.DispatchListener;
import me.ehp246.aufkafka.api.consumer.EndpointAt;
import me.ehp246.aufkafka.api.consumer.InboundEventContext;
import me.ehp246.aufkafka.api.consumer.InvocableFactory;

/**
 * @author Lei Yang
 *
 */
class DefaultInboundEndpointConsumerTest {
    record ExceptionContext(InboundEventContext eventContext, Exception thrown) {
    }

    private final EndpointAt at = new EndpointAt("topic", List.of(0));
    private final TopicPartition partition = new TopicPartition(at.topic(), at.partitions().get(0));
    private final EventInvocableRunnableBuilder dispatcher = (i, r) -> () -> {
    };
    private final InvocableFactory factory = r -> null;

    @Test
    void exception_01() throws InterruptedException, ExecutionException {
        final var commitFuture = new CompletableFuture<Void>();
        final var exceptionRef = new AtomicReference<ExceptionContext>();
        final var msg = new ConsumerRecord<String, String>(at.topic(), at.partitions().get(0), 0, null, null);
        final var consumer = new MockConsumer<String, String>(OffsetResetStrategy.EARLIEST) {

            @Override
            public synchronized void commitSync() {
                super.commitSync();
                commitFuture.complete(null);
            }

            @Override
            public synchronized void assign(Collection<TopicPartition> partitions) {
                super.assign(partitions);
                super.addRecord(msg);
            }

        };

        consumer.updateBeginningOffsets(Map.of(partition, 0L));

        final var thrown = new RuntimeException();

        final var task = new DefaultInboundEndpointConsumer(at, consumer, Duration.ofDays(1)::abs, dispatcher,
                (InvocableFactory) (r -> {
                    throw thrown;
                }), null, null,
                (DispatchListener.ExceptionListener) (e, t) -> exceptionRef.set(new ExceptionContext(e, t)));

        Executors.newVirtualThreadPerTaskExecutor().execute(task::run);

        commitFuture.get();

        final var context = exceptionRef.get();

        Assertions.assertEquals(thrown, context.thrown());
        Assertions.assertEquals(consumer, context.eventContext().consumer());
        Assertions.assertEquals(msg, context.eventContext().event().consumerRecord());
        Assertions.assertEquals(1, consumer.committed(Set.of(partition)).get(partition).offset());
    }

    @Test
    void pollDuration_01() throws InterruptedException, ExecutionException {
        final var expected = Duration.ofDays(2);
        final var pollFuture = new CompletableFuture<Duration>();

        final var consumer = new MockConsumer<String, String>(OffsetResetStrategy.EARLIEST) {

            @Override
            public synchronized ConsumerRecords<String, String> poll(Duration timeout) {
                pollFuture.complete(timeout);
                return super.poll(timeout);
            }

        };

        consumer.updateBeginningOffsets(Map.of(partition, 0L));

        Executors.newVirtualThreadPerTaskExecutor().execute(new DefaultInboundEndpointConsumer(at, consumer,
                () -> expected, dispatcher, factory, null, null, null)::run);

        Assertions.assertEquals(expected, pollFuture.get());
    }
}
