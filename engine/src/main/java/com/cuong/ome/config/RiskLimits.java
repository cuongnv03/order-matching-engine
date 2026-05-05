package com.cuong.ome.config;

public record RiskLimits(long maxOrderQuantity, int maxClientOrderIdLen) {

    public RiskLimits {
        if (maxOrderQuantity <= 0) {
            throw new IllegalArgumentException("maxOrderQuantity must be positive: " + maxOrderQuantity);
        }
        if (maxClientOrderIdLen <= 0) {
            throw new IllegalArgumentException("maxClientOrderIdLen must be positive: " + maxClientOrderIdLen);
        }
    }
}
