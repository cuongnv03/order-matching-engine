package com.cuong.ome.util;

public interface Clock {

    long nowEpochMs();

    long nowNanos();

    Clock SYSTEM = new Clock() {
        @Override public long nowEpochMs() { return System.currentTimeMillis(); }
        @Override public long nowNanos() { return System.nanoTime(); }
    };
}
