package me.ehp246.test.embedded.consumer.pause;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.kafka.test.context.EmbeddedKafka;

import me.ehp246.aufkafka.api.common.AufKafkaConstant;
import me.ehp246.aufkafka.api.producer.OutboundEvent;
import me.ehp246.aufkafka.api.producer.ProducerFnProvider;
import me.ehp246.test.mock.OutboundEventRecord;

@SpringBootTest(classes = { App.class, EmbeddedKafkaConfig.class, Pause.class, }, webEnvironment = WebEnvironment.NONE)
@EmbeddedKafka(topics = { App.TOPIC })
@EnabledIfSystemProperty(named = "me.ehp246.test.embedded.consumer.pause", matches = "true")
class PauseTest {
    private final OutboundEvent.Header PAUSE_EVENT = new OutboundEventRecord.HeaderRecord(AufKafkaConstant.EVENT_HEADER,
	    "Pause");

    @Autowired
    private Pause pause;

    @Autowired
    private ProducerFnProvider provider;

    private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();

    private OutboundEvent.Header correlIdheader(final String id) {
	return new OutboundEventRecord.HeaderRecord(AufKafkaConstant.CORRELATIONID_HEADER, id);
    }

    @Test
    void test_01() throws InterruptedException {
	final var producerFn = provider.get("");

	this.executor.execute(() -> {
	    for (int i = 1; i <= App.REPEAT; i++) {

		producerFn.send(new OutboundEvent() {
		    final String id = UUID.randomUUID().toString();

		    @Override
		    public String topic() {
			return App.TOPIC;
		    }

		    @Override
		    public Object value() {
			return App.MAX_POLL_INTERVAL + 10;
		    }

		    @Override
		    public List<Header> headers() {
			return List.of(correlIdheader(id), PAUSE_EVENT);
		    }
		});
	    }
	});

	Assertions.assertEquals(App.REPEAT, pause.take().size());

    }

}
