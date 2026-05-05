package com.cuong.ome.config;

public record SymbolConfig(long refPrice, int bandBps) {

    public SymbolConfig {
        if (refPrice <= 0) {
            throw new IllegalArgumentException("refPrice must be positive: " + refPrice);
        }
        if (bandBps < 0 || bandBps > 10_000) {
            throw new IllegalArgumentException("bandBps must be in [0, 10000]: " + bandBps);
        }
    }

    public long lowerBand() {
        return Math.multiplyExact(refPrice, 10_000L - bandBps) / 10_000L;
    }

    public long upperBand() {
        return Math.multiplyExact(refPrice, 10_000L + bandBps) / 10_000L;
    }
}
