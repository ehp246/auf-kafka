package me.ehp246.test.embedded.consumer.pause;

import java.time.Duration;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest(classes = { App.class, EmbeddedKafkaConfig.class, Pause.class }, webEnvironment = WebEnvironment.NONE)
@EmbeddedKafka(topics = { App.TOPIC })
class PauseTest {
    @Autowired
    private Pause pause;

    @Autowired
    private Proxy proxy;

    @Test
    void test_01() {
	final var id = UUID.randomUUID().toString();
	proxy.pause(id, 1);

	Assertions.assertEquals(id, pause.take());
    }

    @Test
    void test_02() throws InterruptedException {
	final var id = UUID.randomUUID().toString();
	proxy.pause(id, 1000);

	Assertions.assertEquals(id, pause.take());

	Thread.sleep(Duration.ofSeconds(60));
    }
}
