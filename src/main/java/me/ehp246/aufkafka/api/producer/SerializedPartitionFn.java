package me.ehp246.aufkafka.api.producer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;

/**
 * Maps a {@linkplain OutboundRecord#partitionKey()} to
 * {@linkplain ProducerRecord#partition()} deterministically by converting the
 * object to a byte array via {@linkplain Serializable}.
 * 
 * @author Lei Yang
 * @see PartitionFn
 */
public final class SerializedPartitionFn implements PartitionFn {

    /**
     * <code>null</code> key is mapped to <code>null</code> value.
     * 
     */
    @Override
    public Integer apply(List<PartitionInfo> infos, Object key) {
        if (key == null) {
            return null;
        }

        try (final var byteOutputStream = new ByteArrayOutputStream();
                final var objectOutputStream = new ObjectOutputStream(byteOutputStream);) {
            objectOutputStream.writeObject(key);
            objectOutputStream.flush();
            return Utils.toPositive(Utils.murmur2(byteOutputStream.toByteArray())) % infos.size();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
