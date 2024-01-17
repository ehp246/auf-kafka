package me.ehp246.aufkafka.core.consumer;

import java.time.Duration;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.ehp246.aufkafka.api.consumer.ConsumerExceptionListener;
import me.ehp246.aufkafka.api.consumer.ConsumerListener;
import me.ehp246.aufkafka.api.consumer.InvocableDispatcher;
import me.ehp246.aufkafka.api.consumer.InvocableFactory;
import me.ehp246.aufkafka.api.consumer.UnmatchedConsumer;
import me.ehp246.aufkafka.api.exception.UnknownKeyException;
import me.ehp246.aufkafka.api.spi.MsgMDCContext;

/**
 * @author Lei Yang
 * @since 1.0
 */
final class ConsumerTask implements Runnable {
    private final static Logger LOGGER = LoggerFactory.getLogger(ConsumerTask.class);

    private final Consumer<String, String> consumer;
    private final InvocableDispatcher dispatcher;
    private final InvocableFactory invocableFactory;
    private final ConsumerListener.DispatchingListener onDispatching;
    private final UnmatchedConsumer onUnmatched;
    private final ConsumerExceptionListener onException;

    ConsumerTask(final Consumer<String, String> consumer, final InvocableDispatcher dispatcher,
            final InvocableFactory invocableFactory,
            final ConsumerListener.DispatchingListener onDispatching,
            final UnmatchedConsumer onUnmatched, final ConsumerExceptionListener onException) {
        super();
        this.consumer = consumer;
        this.dispatcher = dispatcher;
        this.invocableFactory = invocableFactory;

        this.onDispatching = onDispatching;
        this.onUnmatched = onUnmatched;
        this.onException = onException;
    }

    @Override
    public void run() {
        while (true) {
            final var polled = consumer.poll(Duration.ofMillis(100));
            if (polled.count() > 1) {
                LOGGER.atWarn().setMessage("Polled count: {}").addArgument(polled::count).log();
            }

            for (final var msg : polled) {
                try (final var closeble = MsgMDCContext.set(msg);) {
                    this.onDispatching.onDispatching(msg);

                    final var invocable = invocableFactory.get(msg);

                    if (invocable == null) {
                        if (onUnmatched == null) {
                            throw new UnknownKeyException(msg);
                        } else {
                            onUnmatched.accept(msg);
                        }
                    } else {
                        dispatcher.dispatch(invocable, msg);
                    }
                } catch (Exception e) {
                    LOGGER.atError().setCause(e)
                            .setMessage(this.onException.getClass().getSimpleName()
                                    + " failed, ignored: {}, {}, {} because of {}")
                            .addArgument(msg::topic).addArgument(msg::key).addArgument(msg::offset)
                            .addArgument(e::getMessage).log();

                    if (this.onException != null) {
                        this.onException.onException(new ConsumerExceptionListener.Context() {

                            @Override
                            public Consumer<String, String> consumer() {
                                return consumer;
                            }

                            @Override
                            public ConsumerRecord<String, String> message() {
                                return msg;
                            }

                            @Override
                            public Exception thrown() {
                                return e;
                            }
                        });
                    }
                }
            }

            if (polled.count() > 0) {
                consumer.commitSync();
            }
        }
    }

}
