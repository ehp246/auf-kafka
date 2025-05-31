package me.ehp246.test.embedded.producerfn;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import me.ehp246.aufkafka.api.producer.OutboundEvent;
import me.ehp246.aufkafka.api.producer.ProducerFnProvider;
import me.ehp246.test.mock.EmbeddedKafkaConfig;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { EmbeddedKafkaConfig.class, AppConfig.class, MsgListener.class })
@EmbeddedKafka(topics = { AppConfig.TOPIC })
@DirtiesContext
class ProducerFnTest {
    @Autowired
    private ProducerFnProvider fnProvider;

    @Autowired
    private MsgListener listener;

    @Test
    void send_01() throws InterruptedException, ExecutionException {
	final var expected = UUID.randomUUID().toString();

	final var fn = fnProvider.get("");

	final var sent = fn.send(new OutboundEvent() {

	    @Override
	    public String topic() {
		return AppConfig.TOPIC;
	    }

	    @Override
	    public String key() {
		return expected;
	    }

	});

	final var event = listener.take();

	Assertions.assertEquals(expected, event.key());
	Assertions.assertEquals(expected, sent.get().producerRecord().key());
	Assertions.assertEquals(AppConfig.TOPIC, sent.get().metadata().topic());
    }
}
