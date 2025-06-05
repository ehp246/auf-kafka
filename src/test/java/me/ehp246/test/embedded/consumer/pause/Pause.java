package me.ehp246.test.embedded.consumer.pause;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Service;

import me.ehp246.aufkafka.api.annotation.Execution;
import me.ehp246.aufkafka.api.annotation.ForEvent;
import me.ehp246.aufkafka.api.annotation.OfHeader;
import me.ehp246.aufkafka.api.annotation.OfValue;
import me.ehp246.aufkafka.api.common.AufKafkaConstant;
import me.ehp246.aufkafka.api.consumer.InstanceScope;
import me.ehp246.aufkafka.core.util.OneUtil;

/**
 * @author Lei Yang
 */
@Service
@ForEvent(execution = @Execution(scope = InstanceScope.BEAN))
public class Pause {
    private final AtomicReference<CompletableFuture<String>> ref = new AtomicReference<>(new CompletableFuture<>());

    public void apply(@OfValue int i, @OfHeader(AufKafkaConstant.CORRELATIONID_HEADER) final String id) {
	try {
	    Thread.sleep(i);
	    this.ref.get().complete(id);
	} catch (InterruptedException e) {
	    throw new RuntimeException(e);
	}
    }

    String take() {
	final var value = OneUtil.orThrow(this.ref.get()::get);
	this.ref.set(new CompletableFuture<String>());
	return value;
    }

    String take(int i) throws InterruptedException, ExecutionException, TimeoutException {
	final var value = this.ref.get().get(i, TimeUnit.MILLISECONDS);
	this.ref.set(new CompletableFuture<String>());
	return value;
    }
}
