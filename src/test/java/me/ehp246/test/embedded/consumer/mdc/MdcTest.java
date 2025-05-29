package me.ehp246.test.embedded.consumer.mdc;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.kafka.test.context.EmbeddedKafka;

import me.ehp246.test.embedded.consumer.mdc.MdcCase.Order;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.NONE)
@EmbeddedKafka(topics = { "embedded" }, partitions = 1)
@Disabled
class MdcTest {
    @Autowired
    private MdcCase case1;
    @Autowired
    private OnPing onPing;
    @Autowired
    private OnPing2 onPing2;
    @Autowired
    private OnPingOnBody onPingOnBody;
    @Autowired
    private Log4jContextInvocationListener invocationListener;

    @BeforeEach
    void clear() {
        MDC.clear();
        onPing.reset();
        invocationListener.reset();
    }

    @Test
    void mdc_01() {
        final var expected = UUID.randomUUID().toString();

        case1.ping(expected);

        Assertions.assertEquals("[" + expected + "]", onPing.take().get("OrderId"));
    }

    @Test
    void mdc_02() {
        case1.ping2(1234, new Order(4321, 2));

        final var remoteContext = onPing2.take();

        Assertions.assertEquals("1234", remoteContext.get("accountId"));
    }

    @Test
    void value_introspect_01() throws InterruptedException, ExecutionException {
        final var order = new Order((int) (Math.random() * 100), (int) (Math.random() * 100));

        case1.pingOnBody(order);

        final var remoteContext = onPingOnBody.take();

        Assertions.assertEquals(order.id() + "", remoteContext.get("Order_OrderId"));
        Assertions.assertEquals(order.amount() + "", remoteContext.get("Order_amount"));
    }

    @Test
    void invocationListener_01() {
        final var accountId = (int) (Math.random() * 100);

        case1.ping2(accountId, new Order((int) (Math.random() * 100), (int) (Math.random() * 100)));

        final var context = invocationListener.take();

        Assertions.assertEquals(accountId + "", context.get("accountId"));
    }
}
