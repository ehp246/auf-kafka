package me.ehp246.aufkafka.core.consumer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.ehp246.aufkafka.api.consumer.ConsumerListener;
import me.ehp246.aufkafka.api.consumer.InvocableDispatcher;
import me.ehp246.aufkafka.api.consumer.InvocableFactory;
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
    private final List<ConsumerListener.UnmatchedListener> onUnmatched;
    private final List<ConsumerListener.ReceivedListener> onReceived;
    private final List<ConsumerListener.ExceptionListener> onException;

    ConsumerTask(final Consumer<String, String> consumer, final InvocableDispatcher dispatcher,
            final InvocableFactory invocableFactory,
            final List<ConsumerListener> eventListeners) {
        super();
        this.consumer = consumer;
        this.dispatcher = dispatcher;
        this.invocableFactory = invocableFactory;

        final var onUnmatched = new ArrayList<ConsumerListener.UnmatchedListener>();
        final var onReceived = new ArrayList<ConsumerListener.ReceivedListener>();
        final var onException = new ArrayList<ConsumerListener.ExceptionListener>();

        for (final var listener : eventListeners) {
            switch (listener) {
                case ConsumerListener.UnmatchedListener l:
                    onUnmatched.add(l);
                    break;
                case ConsumerListener.ReceivedListener l:
                    onReceived.add(l);
                    break;
                case ConsumerListener.ExceptionListener l:
                    onException.add(l);
                    break;
            }
        }

        this.onUnmatched = Collections.unmodifiableList(onUnmatched);
        this.onReceived = Collections.unmodifiableList(onReceived);
        this.onException = Collections.unmodifiableList(onException);
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
                    this.onReceived.forEach(l -> l.onReceived(msg));

                    final var invocable = invocableFactory.get(msg);

                    if (invocable == null) {
                        if (onUnmatched == null) {
                            throw new UnknownKeyException(msg);
                        } else {
                            onUnmatched.stream().forEach(l -> l.onUnmatched(msg));
                        }
                    } else {
                        dispatcher.dispatch(invocable, msg);
                    }
                } catch (Exception e) {
                    try {
                        final var exceptionContext = new ConsumerListener.ExceptionListener.ExceptionContext() {

                            @Override
                            public Consumer<String, String> consumer() {
                                return consumer;
                            }

                            @Override
                            public ConsumerRecord<String, String> received() {
                                return msg;
                            }

                            @Override
                            public Exception thrown() {
                                return e;
                            }
                        };
                        this.onException.stream().forEach(l -> l.onException(exceptionContext));
                    } catch (Exception ex) {
                        LOGGER.atError().setCause(ex)
                                .setMessage(this.onException.getClass().getSimpleName()
                                        + " failed, ignored: {}, {}, {} because of {}")
                                .addArgument(msg::topic).addArgument(msg::key)
                                .addArgument(msg::offset).addArgument(ex::getMessage).log();
                    }
                }
            }

            if (polled.count() > 0) {
                consumer.commitSync();
            }
        }
    }

}
