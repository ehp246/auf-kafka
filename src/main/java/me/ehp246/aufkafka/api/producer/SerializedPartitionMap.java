package me.ehp246.aufkafka.api.producer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;

/**
 * Maps an {@linkplain Object} partition key to a value deterministically by
 * {@linkplain Object#hashCode()}.
 * 
 * @author Lei Yang
 * @see PartitionKeyMap
 */
public final class SerializedPartitionMap implements PartitionKeyMap {

    /**
     * <code>null</code> key is mapped to <code>null</code> value.
     * 
     */
    @Override
    public Integer get(List<PartitionInfo> infos, Object key) {
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
