package com.cuong.ome.book;

public record BookLevel(long price, long totalQuantity, int orderCount) {

    public BookLevel {
        if (price <= 0) {
            throw new IllegalArgumentException("price must be positive: " + price);
        }
        if (totalQuantity < 0) {
            throw new IllegalArgumentException("totalQuantity must be non-negative: " + totalQuantity);
        }
        if (orderCount < 0) {
            throw new IllegalArgumentException("orderCount must be non-negative: " + orderCount);
        }
    }
}
