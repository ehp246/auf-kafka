package me.ehp246.aufkafka.core.producer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.ehp246.aufkafka.api.common.AufKafkaConstant;
import me.ehp246.aufkafka.api.common.Pair;
import me.ehp246.aufkafka.api.producer.ProducerConfigProvider;
import me.ehp246.aufkafka.api.producer.ProducerFn;
import me.ehp246.aufkafka.api.producer.ProducerFnProvider;
import me.ehp246.aufkafka.api.producer.ProducerRecordBuilder;
import me.ehp246.aufkafka.api.producer.ProducerSendRecord;

/**
 * @author Lei Yang
 *
 */
public final class DefaultProducerFnProvider implements ProducerFnProvider, AutoCloseable {
    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultProducerFnProvider.class);

    private final ProducerRecordBuilder recordBuilder;
    private final Function<Map<String, Object>, Producer<String, String>> producerSupplier;
    private final ProducerConfigProvider configProvider;
    private final Map<String, Pair<Producer<String, String>, Boolean>> created = new ConcurrentHashMap<>();

    DefaultProducerFnProvider(final Function<Map<String, Object>, Producer<String, String>> producerSupplier,
	    final ProducerConfigProvider configProvider, final ProducerRecordBuilder recordBuilder) {
	super();
	this.producerSupplier = producerSupplier;
	this.configProvider = configProvider;
	this.recordBuilder = recordBuilder;
    }

    @Override
    public ProducerFn get(final String configName) {
	final var created = getProducer(configName);
	final var producer = created.left();
	final var flush = created.right().booleanValue();

	return outboundEvent -> {
	    final var producerRecord = recordBuilder.apply(outboundEvent);
	    final var sendFuture = new CompletableFuture<RecordMetadata>();

	    producer.send(producerRecord, (metadata, exception) -> {
		if (exception == null) {
		    sendFuture.complete(metadata);
		} else {
		    sendFuture.completeExceptionally(exception);
		}
	    });

	    if (flush) {
		producer.flush();
	    }

	    return new ProducerSendRecord(producerRecord, sendFuture);
	};
    }

    private Pair<Producer<String, String>, Boolean> getProducer(String configName) {
	if (configName == null) {
	    throw new IllegalArgumentException("Configuration name can't be null");
	}

	return this.created.computeIfAbsent(configName, n -> {
	    /*
	     * Global provider first.
	     */
	    final var configMap = new HashMap<>(configProvider.get(n));

	    /*
	     * Required overwrites all others
	     */
	    configMap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
	    configMap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

	    return new Pair<>(producerSupplier.apply(configMap),
		    Optional.ofNullable(configMap.get(AufKafkaConstant.FLUSH_PRODUCER)).map(Object::toString)
			    .map(Boolean::valueOf).orElse(Boolean.FALSE));
	});
    }

    @Override
    public void close() throws Exception {
	created.forEach((name, producer) -> {
	    try {
		producer.left().close();
	    } catch (Exception e) {
		LOGGER.atError().setCause(e).addMarker(AufKafkaConstant.EXCEPTION)
			.setMessage("Producer {} failed to close, ignored.").addArgument(name).log();
	    }
	});

	this.created.clear();
    }
}
