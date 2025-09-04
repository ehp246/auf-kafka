package me.ehp246.aufkafka.core.consumer;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import me.ehp246.aufkafka.api.consumer.BoundInvocable;
import me.ehp246.aufkafka.api.consumer.EventInvocable;
import me.ehp246.aufkafka.api.consumer.EventInvocableBinder;
import me.ehp246.aufkafka.api.consumer.InvocationListener;
import me.ehp246.aufkafka.api.consumer.InvocationListener.CompletedListener;
import me.ehp246.aufkafka.api.consumer.InvocationListener.FailedListener;
import me.ehp246.aufkafka.api.consumer.InvocationListener.InvokingListener;
import me.ehp246.aufkafka.api.consumer.Invoked.Completed;
import me.ehp246.aufkafka.api.consumer.Invoked.Failed;
import me.ehp246.aufkafka.core.provider.jackson.JsonByJackson;
import me.ehp246.aufkafka.core.reflection.ReflectedClass;
import me.ehp246.test.TestUtil;
import me.ehp246.test.TimingExtension;
import me.ehp246.test.mock.InvocableRecord;
import me.ehp246.test.mock.MockConsumerRecord;

/**
 * @author Lei Yang
 *
 */
@ExtendWith(TimingExtension.class)
class DefaultEventInvocableRunnableBuilderTest {
    private final static int LOOP = 1_000_000;
    private final EventInvocable eventInvocable = Mockito.mock(EventInvocable.class);

    private static EventInvocableBinder bindToBound(final BoundInvocable bound, final Completed completed) {
        Mockito.when(bound.invoke()).thenReturn(completed);

        return (i, m) -> bound;
    }

    private static EventInvocableBinder bindToComplete(final Completed completed) {
        final var bound = Mockito.mock(BoundInvocable.class);
        Mockito.when(bound.invoke()).thenReturn(completed);

        return (i, m) -> bound;
    }

    private static EventInvocableBinder bindToFail(final Exception ex) {
        final BoundInvocable bound = Mockito.mock(BoundInvocable.class);

        final var failed = (Failed) () -> ex;
        Mockito.when(bound.invoke()).thenReturn(failed);

        return (i, m) -> bound;
    }

    @Test
    void invoking_02() throws Throwable {
        final var expected = new RuntimeException();
        final var invoking = Mockito.mock(InvokingListener.class);
        final var completed = Mockito.mock(CompletedListener.class);
        final var failed = Mockito.mock(FailedListener.class);
        final var actual = Assertions.assertThrows(RuntimeException.class,
                new DefaultEventInvocableRunnableBuilder(
                        bindToBound(Mockito.mock(BoundInvocable.class), Mockito.mock(Completed.class)),
                        List.of((InvocationListener.InvokingListener) b -> {
                            throw expected;
                        }, invoking, completed, failed))
                        .apply(eventInvocable, new MockConsumerRecord().toEventContext())::run);

        Assertions.assertEquals(actual, expected, "should be the thrown from invocable");

        Mockito.verify(invoking, never()).onInvoking(Mockito.any());
        Mockito.verify(completed, never()).onCompleted(Mockito.any(), Mockito.any());
        Mockito.verify(failed, never()).onFailed(Mockito.any(), Mockito.any());
    }

    @Test
    void invocable_exception_01() {
        final var expected = new RuntimeException();

        final var actual = Assertions.assertThrows(RuntimeException.class,
                new DefaultEventInvocableRunnableBuilder(bindToFail(expected), null).apply(eventInvocable,
                        new MockConsumerRecord().toEventContext())::run);

        Assertions.assertEquals(actual, expected, "should be the thrown from invocable");
    }

    @Test
    void failed_02() {
        final var ref = new Failed[1];
        final var expected = new RuntimeException();

        final var binder = bindToFail(expected);

        final var threw = Assertions.assertThrows(RuntimeException.class,
                new DefaultEventInvocableRunnableBuilder(binder, List.of((InvocationListener.FailedListener) (b, m) -> {
                    ref[0] = m;
                })).apply(eventInvocable, new MockConsumerRecord().toEventContext())::run);

        final var failed = ref[0];

        Assertions.assertEquals(expected, failed.thrown(), "should be the one thrown by application code");
        Assertions.assertEquals(expected, threw);
    }

    @Test
    void failed_03() {
        final var expected = new NullPointerException();

        final var actual = Assertions.assertThrows(RuntimeException.class,
                new DefaultEventInvocableRunnableBuilder(bindToFail(new IllegalArgumentException()),
                        List.of((InvocationListener.FailedListener) (b, m) -> {
                            throw expected;
                        })).apply(eventInvocable, new MockConsumerRecord().toEventContext())::run,
                "should allow the listener to throw back to the broker");

        Assertions.assertEquals(expected, actual.getSuppressed()[0], "should have it as suppressed");
    }

    @Test
    void failed_04() {
        final var ref = new Failed[2];
        final var failure = new RuntimeException();
        final var supressed = new NullPointerException();

        final var actual = Assertions.assertThrows(RuntimeException.class,
                new DefaultEventInvocableRunnableBuilder(bindToFail(failure),
                        List.of((InvocationListener.FailedListener) (b, m) -> {
                            ref[0] = m;
                            throw supressed;
                        }, (InvocationListener.FailedListener) (b, m) -> {
                            ref[1] = m;
                            throw supressed;
                        })).apply(eventInvocable, new MockConsumerRecord().toEventContext())::run);

        Assertions.assertEquals(failure, actual, "should be from the invoker");
        Assertions.assertEquals(actual, ref[0].thrown(), "should call with best effort");
        Assertions.assertEquals(actual, ref[1].thrown(), "should call with best effort");
        Assertions.assertEquals(actual.getSuppressed().length, 2);
        Assertions.assertEquals(actual.getSuppressed()[0], supressed);
        Assertions.assertEquals(actual.getSuppressed()[1], supressed);
    }

    @Test
    void thread_01() {
        // Binder, listeners
        final var threadRef = new Thread[2];

        Assertions.assertThrows(IllegalArgumentException.class, new DefaultEventInvocableRunnableBuilder((i, m) -> {
            threadRef[0] = Thread.currentThread();
            final var bound = Mockito.mock(BoundInvocable.class);
            Mockito.when(bound.invoke()).thenReturn(new Failed() {
                private final Exception e = new IllegalArgumentException();

                @Override
                public Throwable thrown() {
                    return e;
                }
            });
            return bound;
        }, List.of((InvocationListener.FailedListener) (b, m) -> {
            threadRef[1] = Thread.currentThread();
        })).apply(eventInvocable, new MockConsumerRecord().toEventContext())::run);

        Assertions.assertEquals(threadRef[0], threadRef[1], "should be the same thread for binder, failed listener");
    }

    @Test
    void completed_02() {
        final var expected = new RuntimeException("Completed");

        final var actual = Assertions.assertThrows(RuntimeException.class,
                new DefaultEventInvocableRunnableBuilder(bindToComplete(Mockito.mock(Completed.class)),
                        List.of((InvocationListener.CompletedListener) (b, c) -> {
                            throw expected;
                        })).apply(eventInvocable, new MockConsumerRecord().toEventContext())::run);

        Assertions.assertEquals(expected, actual, "should be thrown the broker");
    }

    @Test
    void completed_close_01() throws Exception {
        final var completed = Mockito.mock(CompletedListener.class);

        Mockito.doThrow(new IllegalStateException("Don't close me")).when(eventInvocable).close();

        new DefaultEventInvocableRunnableBuilder(bindToComplete(Mockito.mock(Completed.class)), List.of(completed))
                .apply(eventInvocable, new MockConsumerRecord().toEventContext()).run();

        Mockito.verify(eventInvocable, times(1)).close();
        // Exception from the close should be suppressed.
        Mockito.verify(completed, times(1)).onCompleted(Mockito.any(BoundInvocable.class),
                Mockito.any(Completed.class));
    }

    @Test
    void close_01() throws Exception {
        new DefaultEventInvocableRunnableBuilder(bindToComplete(Mockito.mock(Completed.class)), null)
                .apply(eventInvocable, new MockConsumerRecord().toEventContext()).run();

        // Should close on completed invocation
        Mockito.verify(eventInvocable, times(1)).close();
    }

    @Test
    void close_02() throws Exception {
        Assertions.assertThrows(RuntimeException.class,
                new DefaultEventInvocableRunnableBuilder(bindToFail(new RuntimeException()), null).apply(eventInvocable,
                        new MockConsumerRecord().toEventContext())::run);

        // Should close on failed invocation
        Mockito.verify(eventInvocable, times(1)).close();
    }

    @Test
    void close_03() throws Exception {
        Assertions.assertThrows(RuntimeException.class, new DefaultEventInvocableRunnableBuilder((i, m) -> null, null)
                .apply(eventInvocable, new MockConsumerRecord().toEventContext())::run);

        // Should close on wrong data
        Mockito.verify(eventInvocable, times(1)).close();
    }

    @Test
    void close_04() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, new DefaultEventInvocableRunnableBuilder((i, m) -> {
            throw new IllegalArgumentException();
        }, null).apply(eventInvocable, new MockConsumerRecord().toEventContext())::run);

        // Should close on binder exception
        Mockito.verify(eventInvocable, times(1)).close();
    }

    @Test
    @EnabledIfSystemProperty(named = "me.ehp246.perf", matches = "true")
    void perf_01() {
        final var binder = new DefaultEventInvocableBinder(new JsonByJackson(TestUtil.OBJECT_MAPPER));
        final var dispatcher = new DefaultEventInvocableRunnableBuilder(binder, null);
        final var msg = new MockConsumerRecord();
        final var invocable = new InvocableRecord(new InvocableBinderTestCases.PerfCase(),
                new ReflectedClass<>(InvocableBinderTestCases.PerfCase.class).findMethods("m01").get(0));

        IntStream.range(0, LOOP).forEach(i -> dispatcher.apply(invocable, msg.toEventContext()).run());
    }

    @Test
    void log4jConext_01() {
        final var contextRef = new Map[2];
        final var key = UUID.randomUUID().toString();
        final var context = Map.of(key, UUID.randomUUID().toString());

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new DefaultEventInvocableRunnableBuilder((i, m) -> {
                    final var bound = Mockito.mock(BoundInvocable.class);
                    Mockito.when(bound.mdcMap()).thenReturn(context);
                    Mockito.when(bound.invoke()).then(new Answer<Object>() {

                        @Override
                        public Object answer(final InvocationOnMock invocation) throws Throwable {
                            contextRef[0] = ThreadContext.getContext();
                            return new Failed() {
                                private final Exception e = new IllegalArgumentException();

                                @Override
                                public Throwable thrown() {
                                    return e;
                                }
                            };
                        }
                    });
                    return bound;
                }, List.of((InvocationListener.FailedListener) (b, m) -> {
                    contextRef[1] = ThreadContext.getContext();
                })).apply(eventInvocable, new MockConsumerRecord().toEventContext()).run());

        Assertions.assertEquals(null, ThreadContext.get(key), "should clean up");
        Assertions.assertEquals(context.get(key), contextRef[0].get(key), "should be there for the invoke");
        Assertions.assertEquals(context.get(key), contextRef[1].get(key), "should be there for the listeners");
    }

    @Test
    void log4jConext_02() {
        final var contextRef = new Map[2];

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new DefaultEventInvocableRunnableBuilder((i, m) -> {
                    final var bound = Mockito.mock(BoundInvocable.class);
                    Mockito.when(bound.invoke()).then(new Answer<Object>() {

                        @Override
                        public Object answer(final InvocationOnMock invocation) throws Throwable {
                            contextRef[0] = ThreadContext.getContext();
                            return new Failed() {
                                private final Exception e = new IllegalArgumentException();

                                @Override
                                public Throwable thrown() {
                                    return e;
                                }
                            };
                        }
                    });
                    return bound;
                }, List.of((InvocationListener.FailedListener) (b, m) -> {
                    contextRef[1] = ThreadContext.getContext();
                })).apply(eventInvocable, new MockConsumerRecord().toEventContext()).run());

        Assertions.assertEquals(0, ThreadContext.getContext().size());
        Assertions.assertEquals(0, contextRef[0].size());
        Assertions.assertEquals(0, contextRef[1].size());
    }
}
