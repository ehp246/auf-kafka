package me.ehp246.aufkafka.api.spi;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import me.ehp246.aufkafka.core.util.OneUtil;
import me.ehp246.test.mock.MockConsumerRecord;

class EventMdcContextTest {
    private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();

    @Test
    void mdcHeaders_01() {
        final var completed = new CompletableFuture<String>();
        final var key = UUID.randomUUID().toString();
        final var value = UUID.randomUUID().toString();
        final var event = MockConsumerRecord.withHeaders(key, value).toEventContext();

        executor.execute(() -> {
            EventMdcContext.setMdcHeaders(Set.of(key));
            EventMdcContext.set(event);
            completed.complete(MDC.get(key));
        });

        Assertions.assertEquals(List.of(value).toString(), OneUtil.orThrow(completed::get));
        Assertions.assertEquals(null, MDC.get(key));
    }

    @Test
    void mdcHeaders_02() {
        final var completed = new CompletableFuture<String>();
        final var key = UUID.randomUUID().toString();
        final var value = UUID.randomUUID().toString();
        final var event = MockConsumerRecord.withHeaders(key, value).toEventContext();

        executor.execute(() -> {
            EventMdcContext.set(event);
            completed.complete(MDC.get(key));
        });

        Assertions.assertEquals((String) null, OneUtil.orThrow(completed::get));
        Assertions.assertEquals((String) null, MDC.get(key));
    }

    @Test
    void mdcHeaders_03() {
        final var completed = new CompletableFuture<String>();
        final var key = UUID.randomUUID().toString();
        final var value = UUID.randomUUID().toString();
        final var event = MockConsumerRecord.withHeaders(key, value).toEventContext();

        EventMdcContext.setMdcHeaders(Set.of(key));
        executor.execute(() -> {
            EventMdcContext.set(event);
            completed.complete(MDC.get(key));
        });

        Assertions.assertEquals((String) null, OneUtil.orThrow(completed::get));
        Assertions.assertEquals((String) null, MDC.get(key));
    }

    @Test
    void mdcHeaders_04() {
        final var completed = new CompletableFuture<String>();
        final var key = UUID.randomUUID().toString();
        final var value = UUID.randomUUID().toString();
        final var event = MockConsumerRecord.withHeaders(key, value).toEventContext();

        executor.execute(() -> {
            try (final var closable = EventMdcContext.setMdcHeaders(Set.of(key))) {
                EventMdcContext.set(event).close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            completed.complete(MDC.get(key));
        });

        Assertions.assertEquals((String) null, OneUtil.orThrow(completed::get));
        Assertions.assertEquals((String) null, MDC.get(key));
    }

    @Test
    void mdcHeaders_05() {
        final var completed = new CompletableFuture<String>();
        final var key = UUID.randomUUID().toString();
        final var value = UUID.randomUUID().toString();
        final var event = MockConsumerRecord.withHeaders(key, value).toEventContext();

        executor.execute(() -> {
            try (final var closable = EventMdcContext.setMdcHeaders(Set.of(key))) {
                EventMdcContext.set(event).close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            EventMdcContext.set(MockConsumerRecord.withHeaders(key, UUID.randomUUID().toString()).toEventContext());
            completed.complete(MDC.get(key));
        });

        Assertions.assertEquals((String) null, OneUtil.orThrow(completed::get),
                "should not have the context after closing the event.");
        Assertions.assertEquals((String) null, MDC.get(key));
    }

    @Test
    void mdcHeaders_06() {
        final var completed = new CompletableFuture<String>();
        final var key = UUID.randomUUID().toString();
        final var event = MockConsumerRecord.withHeaders(key, null).toEventContext();

        executor.execute(() -> {
            EventMdcContext.setMdcHeaders(Set.of(key));
            EventMdcContext.set(event);
            completed.complete(MDC.get(key));
        });

        Assertions.assertEquals("[null]", OneUtil.orThrow(completed::get));
        Assertions.assertEquals((String) null, MDC.get(key));
    }

    @Test
    void mdcHeaders_07() {
        final var completed = new CompletableFuture<String>();
        final var missingHeder = UUID.randomUUID().toString();
        final var event = MockConsumerRecord.withHeaders(UUID.randomUUID().toString(), UUID.randomUUID().toString())
                .toEventContext();

        executor.execute(() -> {
            EventMdcContext.setMdcHeaders(Set.of(missingHeder));
            EventMdcContext.set(event);
            completed.complete(MDC.get(missingHeder));
        });

        Assertions.assertEquals((String) null, OneUtil.orThrow(completed::get));
        Assertions.assertEquals((String) null, MDC.get(missingHeder));
    }

    @Test
    void mdcHeaders_08() {
        final var completed = new CompletableFuture<String>();
        executor.execute(() -> {
            EventMdcContext.setMdcHeaders(null);
            EventMdcContext.set(null);
            completed.complete("");
        });

        Assertions.assertEquals("", OneUtil.orThrow(completed::get));
    }
}
