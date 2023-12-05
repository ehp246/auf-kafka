package me.ehp246.aufkafka.api.producer;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.apache.kafka.common.PartitionInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * @author Lei Yang
 *
 */
class SerializedPartitionMapTest {
    final SerializedPartitionMap map = new SerializedPartitionMap();

    @Test
    void test_01() {
        final var infos = List.of(Mockito.mock(PartitionInfo.class), Mockito.mock(PartitionInfo.class), Mockito.mock(PartitionInfo.class));

        Assertions.assertEquals(2, map.get(infos, "97e9ed90-4f2e-4c72-b9fd-9f4eaf5d259c").intValue());
    }

    @Test
    void test_02() {
        final var infos = List.of(Mockito.mock(PartitionInfo.class), Mockito.mock(PartitionInfo.class), Mockito.mock(PartitionInfo.class));

        Assertions.assertEquals(null, map.get(infos, null));
    }
    
    @Test
    void test_03() {
        final var infos = List.of(Mockito.mock(PartitionInfo.class), Mockito.mock(PartitionInfo.class), Mockito.mock(PartitionInfo.class));

        Assertions.assertEquals(1, map.get(infos, "").intValue());
    }
    
    @Test
    void test_04() {
        final var infos = List.of(Mockito.mock(PartitionInfo.class), Mockito.mock(PartitionInfo.class), Mockito.mock(PartitionInfo.class));

        Assertions.assertEquals(1, map.get(infos, Long.valueOf(20000000L)).intValue());
    }
    
    @Test
    void test_05() {
        final var infos = List.of(Mockito.mock(PartitionInfo.class), Mockito.mock(PartitionInfo.class), Mockito.mock(PartitionInfo.class));

        Assertions.assertEquals(0, map.get(infos, UUID.fromString("74770d6d-a15e-48e0-9949-cbad7f208e26")).intValue());
    }
}
