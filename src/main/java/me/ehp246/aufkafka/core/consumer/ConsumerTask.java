package me.ehp246.aufkafka.core.consumer;

import java.time.Duration;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.ehp246.aufkafka.api.AufKafkaConstant;
import me.ehp246.aufkafka.api.consumer.InvocableDispatcher;
import me.ehp246.aufkafka.api.consumer.InvocableFactory;
import me.ehp246.aufkafka.api.consumer.ReceivedListener;
import me.ehp246.aufkafka.api.exception.UnknownKeyException;
import me.ehp246.aufkafka.api.spi.MsgMDCContext;
import me.ehp246.aufkafka.core.consumer.ConsumptionExceptionListener.Context;

/**
 * @author Lei Yang
 *
 */
final class ConsumerTask implements Runnable {
    private final static Logger LOGGER = LoggerFactory.getLogger(ConsumerTask.class);

    private final Consumer<String, String> consumer;
    private final InvocableDispatcher dispatcher;
    private final InvocableFactory invocableFactory;
    private final ReceivedListener defaultReceivedListener;
    private final ConsumptionExceptionListener consumerExceptionListener;

    ConsumerTask(final Consumer<String, String> consumer, final InvocableDispatcher dispatcher,
            final InvocableFactory invocableFactory, final ReceivedListener defaultReceivedListener,
            final ConsumptionExceptionListener consumerExceptionListener) {
        super();
        this.consumer = consumer;
        this.dispatcher = dispatcher;
        this.invocableFactory = invocableFactory;
        this.defaultReceivedListener = defaultReceivedListener;
        this.consumerExceptionListener = consumerExceptionListener;
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
                    LOGGER.atDebug().setMessage("{}, {}").addArgument(msg::topic)
                            .addArgument(msg::key).log();

                    LOGGER.atTrace().addMarker(AufKafkaConstant.VALUE).setMessage("{}")
                            .addArgument(msg::value).log();

                    final var invocable = invocableFactory.get(msg);

                    if (invocable == null) {
                        if (defaultReceivedListener == null) {
                            throw new UnknownKeyException(msg);
                        } else {
                            defaultReceivedListener.apply(msg);
                        }
                    } else {
                        dispatcher.dispatch(invocable, msg);
                    }
                } catch (Exception e) {
                    try {
                        this.consumerExceptionListener.apply(new Context() {

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
                        });
                    } catch (Exception ex) {
                        LOGGER.atError().setCause(ex)
                                .setMessage(
                                        this.consumerExceptionListener.getClass().getSimpleName()
                                                + " failed, ignored: {}, {}, {} because of {}")
                                .addArgument(msg::topic).addArgument(msg::key)
                                .addArgument(msg::offset).addArgument(ex::getMessage).log();
                    }
                }

                consumer.commitSync();
            }
        }
    }

}
