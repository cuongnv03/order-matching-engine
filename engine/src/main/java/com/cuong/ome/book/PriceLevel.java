package com.cuong.ome.book;

import com.cuong.ome.domain.Order;

import java.util.ArrayDeque;
import java.util.Deque;

final class PriceLevel {

    private final long price;
    private final Deque<Order> orders;
    private long totalQuantity;

    PriceLevel(long price) {
        if (price <= 0) {
            throw new IllegalArgumentException("price must be positive: " + price);
        }
        this.price = price;
        this.orders = new ArrayDeque<>();
        this.totalQuantity = 0L;
    }

    void appendOrder(Order order) {
        orders.addLast(order);
        totalQuantity += order.remainingQuantity();
    }

    boolean removeOrder(Order order) {
        if (orders.removeFirstOccurrence(order)) {
            totalQuantity -= order.remainingQuantity();
            return true;
        }
        return false;
    }

    long price() { return price; }
    long totalQuantity() { return totalQuantity; }
    int orderCount() { return orders.size(); }
    boolean isEmpty() { return orders.isEmpty(); }
    Deque<Order> orders() { return orders; }
}
