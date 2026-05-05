package com.cuong.ome.util;

import java.security.SecureRandom;
import java.util.UUID;

public final class IdGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private IdGenerator() {
    }

    public static String newOrderId() {
        return uuidV7().toString();
    }

    public static String newTradeId() {
        return uuidV7().toString();
    }

    public static UUID uuidV7() {
        long timestampMs = System.currentTimeMillis();
        byte[] bytes = new byte[10];
        RANDOM.nextBytes(bytes);

        long msb = (timestampMs & 0xFFFFFFFFFFFFL) << 16;
        msb |= 0x7000L;
        msb |= ((bytes[0] & 0x0FL) << 8) | (bytes[1] & 0xFFL);

        long lsb = (((long) bytes[2] & 0x3FL) | 0x80L) << 56;
        for (int i = 3; i < 10; i++) {
            lsb |= ((long) bytes[i] & 0xFFL) << ((9 - i) * 8);
        }
        return new UUID(msb, lsb);
    }

    public static long extractTimestampMs(UUID v7) {
        return v7.getMostSignificantBits() >>> 16;
    }
}
