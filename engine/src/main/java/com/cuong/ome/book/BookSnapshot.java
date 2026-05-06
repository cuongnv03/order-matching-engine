package com.cuong.ome.book;

import java.util.List;
import java.util.Objects;

public record BookSnapshot(
        String symbol,
        List<BookLevel> bids,
        List<BookLevel> asks,
        long timestampEpochMs,
        long sequence) {

    public BookSnapshot {
        Objects.requireNonNull(symbol, "symbol");
        Objects.requireNonNull(bids, "bids");
        Objects.requireNonNull(asks, "asks");
        if (sequence < 0) {
            throw new IllegalArgumentException("sequence must be non-negative: " + sequence);
        }
        bids = List.copyOf(bids);
        asks = List.copyOf(asks);
    }
}
