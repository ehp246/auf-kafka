package me.ehp246.test.embedded.consumer.pause;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.kafka.test.context.EmbeddedKafka;

import me.ehp246.aufkafka.api.common.AufKafkaConstant;
import me.ehp246.aufkafka.api.producer.OutboundEvent;
import me.ehp246.aufkafka.api.producer.ProducerFnProvider;

@SpringBootTest(classes = { App.class, EmbeddedKafkaConfig.class, Pause.class }, webEnvironment = WebEnvironment.NONE)
@EmbeddedKafka(topics = { App.TOPIC })
class PauseTest {
    private final OutboundEvent.Header PAUSE_EVENT = new OutboundEvent.Header() {

	@Override
	public String key() {
	    return AufKafkaConstant.EVENT_HEADER;
	}

	@Override
	public Object value() {
	    return "Pause";
	}

    };

    @Autowired
    private Pause pause;

    @Autowired
    private ProducerFnProvider provider;

    private OutboundEvent.Header correlIdheader(final String id) {
	return new OutboundEvent.Header() {

	    @Override
	    public String key() {
		return AufKafkaConstant.CORRELATIONID_HEADER;
	    }

	    @Override
	    public Object value() {
		return id;
	    }

	};
    }

    @Test
    void test_01() {
	final var id = UUID.randomUUID().toString();

	provider.get("").send(new OutboundEvent() {

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
		return App.MAX_INTERVAL + 10;
	    }

	    @Override
	    public List<Header> headers() {
		return List.of(correlIdheader(id), PAUSE_EVENT);
	    }
	});

	Assertions.assertEquals(id, pause.take());
    }
}
