package me.ehp246.aufkafka.core.producer;

import java.util.concurrent.CompletableFuture;

import me.ehp246.aufkafka.api.producer.PartitionMapProvider;
import me.ehp246.aufkafka.api.producer.ProducerFn;
import me.ehp246.aufkafka.api.producer.ProducerFn.Sent;
import me.ehp246.aufkafka.api.producer.ProducerFnProvider;
import me.ehp246.aufkafka.api.producer.ProducerProvider;
import me.ehp246.aufkafka.api.producer.ProducerRecordBuilderProvider;

/**
 * @author Lei Yang
 *
 */
public final class DefaultProducerFnProvider implements ProducerFnProvider {
	private final ProducerProvider producerProvider;
	private final ProducerRecordBuilderProvider recordBuilderProvider;
	private final PartitionMapProvider partitionKeyMapProvider;

	DefaultProducerFnProvider(final ProducerProvider producerProvider,
			final PartitionMapProvider partitionKeyMapProvider,
			final ProducerRecordBuilderProvider recordBuilderProvider) {
		super();
		this.producerProvider = producerProvider;
		this.partitionKeyMapProvider = partitionKeyMapProvider;
		this.recordBuilderProvider = recordBuilderProvider;
	}

	@Override
	public ProducerFn get(final ProducerFnConfig config) {
		final var producer = producerProvider.get(config.producerConfigName(), config.producerProperties());
		final var recordBuilder = recordBuilderProvider.apply(topic -> producer.partitionsFor(topic),
				this.partitionKeyMapProvider.get(config.partitionMapType()));

		return outboundRecord -> {
			final var producerRecord = recordBuilder.apply(outboundRecord);
			final var completeableFuture = new CompletableFuture<Sent>();

			producer.send(producerRecord, (metadata, exception) -> {
				if (exception == null) {
					completeableFuture.complete(new Sent(producerRecord, metadata));
				} else {
					completeableFuture.completeExceptionally(exception);
				}
			});

			return completeableFuture;
		};
	}

}
