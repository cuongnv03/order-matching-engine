package com.cuong.ome.domain;

public record Trade(
        String id,
        String symbol,
        String buyOrderId,
        String sellOrderId,
        long price,
        long quantity,
        long timestampEpochMs) {

    public Trade {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id required");
        }
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("symbol required");
        }
        if (buyOrderId == null || buyOrderId.isBlank()) {
            throw new IllegalArgumentException("buyOrderId required");
        }
        if (sellOrderId == null || sellOrderId.isBlank()) {
            throw new IllegalArgumentException("sellOrderId required");
        }
        if (buyOrderId.equals(sellOrderId)) {
            throw new IllegalArgumentException("self-trade not allowed: " + buyOrderId);
        }
        if (price <= 0) {
            throw new IllegalArgumentException("price must be positive: " + price);
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive: " + quantity);
        }
    }
}
