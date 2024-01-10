package me.ehp246.aufkafka.core.consumer;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.ehp246.aufkafka.api.consumer.ConsumerFn;
import me.ehp246.aufkafka.api.consumer.InvocableDispatcher;
import me.ehp246.aufkafka.api.consumer.InvocableFactory;
import me.ehp246.test.mock.MockConsumerRecord;

/**
 * @author Lei Yang
 *
 */
class ConsumerTaskTest {
    @SuppressWarnings("unchecked")
    private final Consumer<String, String> consumer = Mockito.mock(Consumer.class);
    private final InvocableDispatcher dispatcher = (i, r) -> {
    };
    private final InvocableFactory factory = r -> null;
    private final ConsumerFn listener = r -> {
    };
    private final ConsumptionExceptionListener exceptionListener = c -> {
    };

    @SuppressWarnings("unchecked")
    @Test
    void exception_01() throws InterruptedException, ExecutionException {
        final var ref = new CompletableFuture<ConsumptionExceptionListener.Context>();
        final var msg = new MockConsumerRecord();
        final var records = Mockito.mock(ConsumerRecords.class);
        Mockito.when(records.count()).thenReturn(1);
        Mockito.when(records.iterator()).thenReturn(List.of(msg).iterator());

        final var consumer = Mockito.mock(Consumer.class);
        Mockito.when(consumer.poll(Mockito.any())).thenReturn(records);

        final var thrown = new RuntimeException();
        final var task = new ConsumerTask(consumer, dispatcher, r -> {
            throw thrown;
        }, listener, c -> ref.complete(c));

        Executors.newVirtualThreadPerTaskExecutor().execute(task);

        final var context = ref.get();

        Assertions.assertEquals(consumer, context.consumer());
        Assertions.assertEquals(thrown, context.thrown());
        Assertions.assertEquals(msg, context.received());

        Mockito.verify(consumer, Mockito.atLeastOnce()).commitSync();
    }

}
