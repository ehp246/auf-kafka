package me.ehp246.test.embedded.consumer.pause;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
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
    private final Set<String> ids = new HashSet<>();
    private final AtomicReference<CompletableFuture<Object>> ref = new AtomicReference<>(new CompletableFuture<>());

    public void apply(@OfValue int i, @OfHeader(AufKafkaConstant.CORRELATIONID_HEADER) final String id) {
	try {
	    Thread.sleep(i);
	    this.ids.add(id);
	    if (this.ids.size() == App.REPEAT) {
		this.ref.get().complete(null);
	    }
	} catch (InterruptedException e) {
	    throw new RuntimeException(e);
	}
    }

    Set<String> take() {
	OneUtil.orThrow(this.ref.get()::get);
	final var value = new HashSet<>(this.ids);
	this.ref.set(new CompletableFuture<>());
	this.ids.clear();
	return value;
    }
}
