package com.cuong.ome.util;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IdGeneratorTest {

    @Test
    void should_generate_1000_unique_ids() {
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            ids.add(IdGenerator.newOrderId());
        }
        assertThat(ids).hasSize(1000);
    }

    @Test
    void should_set_version_7_in_msb() {
        UUID id = IdGenerator.uuidV7();
        int version = (int) ((id.getMostSignificantBits() >>> 12) & 0xF);
        assertThat(version).isEqualTo(7);
    }

    @Test
    void should_set_rfc4122_variant_in_lsb() {
        UUID id = IdGenerator.uuidV7();
        int variant = (int) ((id.getLeastSignificantBits() >>> 62) & 0x3);
        assertThat(variant).isEqualTo(0b10);
    }

    @Test
    void timestamps_should_be_monotonic_non_decreasing() {
        long previous = 0;
        for (int i = 0; i < 1000; i++) {
            long ts = IdGenerator.extractTimestampMs(IdGenerator.uuidV7());
            assertThat(ts).isGreaterThanOrEqualTo(previous);
            previous = ts;
        }
    }

    @Test
    void extracted_timestamp_should_be_within_recent_window() {
        long beforeMs = System.currentTimeMillis();
        UUID id = IdGenerator.uuidV7();
        long afterMs = System.currentTimeMillis();

        long extracted = IdGenerator.extractTimestampMs(id);
        assertThat(extracted).isBetween(beforeMs, afterMs);
    }

    @Test
    void newOrderId_and_newTradeId_should_produce_valid_uuid_strings() {
        String orderId = IdGenerator.newOrderId();
        String tradeId = IdGenerator.newTradeId();

        assertThat(UUID.fromString(orderId)).isNotNull();
        assertThat(UUID.fromString(tradeId)).isNotNull();
    }
}
