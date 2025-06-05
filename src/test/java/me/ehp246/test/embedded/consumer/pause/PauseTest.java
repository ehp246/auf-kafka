package me.ehp246.test.embedded.consumer.pause;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.consumer.CommitFailedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.kafka.test.context.EmbeddedKafka;

import me.ehp246.aufkafka.api.common.AufKafkaConstant;
import me.ehp246.aufkafka.api.producer.OutboundEvent;
import me.ehp246.aufkafka.api.producer.OutboundEventRecord;
import me.ehp246.aufkafka.api.producer.ProducerFnProvider;

@SpringBootTest(classes = { App.class, EmbeddedKafkaConfig.class, Pause.class,
	ConsumerListener.class }, webEnvironment = WebEnvironment.NONE)
@EmbeddedKafka(topics = { App.TOPIC })
class PauseTest {
    private final OutboundEvent.Header PAUSE_EVENT = new OutboundEventRecord.HeaderRecord(AufKafkaConstant.EVENT_HEADER,
	    "Pause");

    @Autowired
    private Pause pause;
    @Autowired
    private ConsumerListener consumerListener;

    @Autowired
    private ProducerFnProvider provider;

    private OutboundEvent.Header correlIdheader(final String id) {
	return new OutboundEventRecord.HeaderRecord(AufKafkaConstant.CORRELATIONID_HEADER, id);
    }

    @Test
    void test_01() throws InterruptedException {
	final var id = UUID.randomUUID().toString();

	final var producerFn = provider.get("");

	producerFn.send(new OutboundEvent() {

	    @Override
	    public String topic() {
		return App.TOPIC;
	    }

	    @Override
	    public Object value() {
		return 1;
	    }

	    @Override
	    public List<Header> headers() {
		return List.of(correlIdheader(id), PAUSE_EVENT);
	    }
	});

	Assertions.assertEquals(id, pause.take());

	Thread.sleep(1000);

	final var id2 = UUID.randomUUID().toString();
	producerFn.send(new OutboundEvent() {

	    @Override
	    public String topic() {
		return App.TOPIC;
	    }

	    @Override
	    public Object value() {
		return 10;
	    }

	    @Override
	    public List<Header> headers() {
		return List.of(correlIdheader(id2), PAUSE_EVENT);
	    }
	});

	Assertions.assertEquals(id2, pause.take());
    }

    @Test
    void test_02() throws InterruptedException {
	final var id = UUID.randomUUID().toString();

	provider.get("").send(new OutboundEvent() {

	    @Override
	    public String topic() {
		return App.TOPIC;
	    }

	    @Override
	    public Object value() {
		return App.MAX_INTERVAL + 100;
	    }

	    @Override
	    public List<Header> headers() {
		return List.of(correlIdheader(id), PAUSE_EVENT);
	    }
	});

	Assertions.assertEquals(id, pause.take());
	Assertions.assertEquals(true, this.consumerListener.take() instanceof CommitFailedException);
    }

    @Test
    void test_03() throws InterruptedException {
	final var id1 = UUID.randomUUID().toString();

	final var producerFn = provider.get("");

	producerFn.send(new OutboundEvent() {

	    @Override
	    public String topic() {
		return App.TOPIC;
	    }

	    @Override
	    public Object value() {
		return App.MAX_INTERVAL + 100;
	    }

	    @Override
	    public List<Header> headers() {
		return List.of(correlIdheader(id1), PAUSE_EVENT);
	    }
	});

	Assertions.assertEquals(id1, pause.take());

	Thread.sleep(1000);

	final var id2 = UUID.randomUUID().toString();
	producerFn.send(new OutboundEvent() {

	    @Override
	    public String topic() {
		return App.TOPIC;
	    }

	    @Override
	    public Object value() {
		return App.MAX_INTERVAL + 100;
	    }

	    @Override
	    public List<Header> headers() {
		return List.of(correlIdheader(id2), PAUSE_EVENT);
	    }
	});

	Assertions.assertThrows(TimeoutException.class, () -> pause.take(1000));
    }
}
