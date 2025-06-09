package me.ehp246.test.mock;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.consumer.ConsumerGroupMetadata;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.ProducerFencedException;

/**
 * @author Lei Yang
 *
 */
public class MockProducer implements Producer<String, String> {
    private boolean closed = false;
    private AtomicInteger flushed = new AtomicInteger(0);

    @Override
    public void initTransactions() {
	// TODO Auto-generated method stub

    }

    @Override
    public void beginTransaction() throws ProducerFencedException {
	// TODO Auto-generated method stub

    }

    @Override
    public void sendOffsetsToTransaction(Map<TopicPartition, OffsetAndMetadata> offsets, String consumerGroupId)
	    throws ProducerFencedException {
	// TODO Auto-generated method stub

    }

    @Override
    public void sendOffsetsToTransaction(Map<TopicPartition, OffsetAndMetadata> offsets,
	    ConsumerGroupMetadata groupMetadata) throws ProducerFencedException {
	// TODO Auto-generated method stub

    }

    @Override
    public void commitTransaction() throws ProducerFencedException {
	// TODO Auto-generated method stub

    }

    @Override
    public void abortTransaction() throws ProducerFencedException {
	// TODO Auto-generated method stub

    }

    @Override
    public Future<RecordMetadata> send(ProducerRecord<String, String> record) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Future<RecordMetadata> send(ProducerRecord<String, String> record, Callback callback) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void flush() {
	this.flushed.incrementAndGet();
    }

    public boolean isFlushed() {
	return this.flushed.get() > 0;
    }

    @Override
    public List<PartitionInfo> partitionsFor(String topic) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Map<MetricName, ? extends Metric> metrics() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void close() {
	this.closed = true;
    }

    @Override
    public void close(Duration timeout) {
	this.closed = true;
    }

    public boolean isClosed() {
	return this.closed;
    }
}
