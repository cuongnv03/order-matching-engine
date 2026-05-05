package com.cuong.ome.domain;

import java.util.Objects;

public final class Order {

    private final String id;
    private final String clientOrderId;
    private final String symbol;
    private final Side side;
    private final OrderType type;
    private final long price;
    private final long quantity;
    private final long submitTimeNanos;
    private final long submitTimeEpochMs;

    private long remainingQuantity;
    private OrderStatus status;

    public Order(
            String id,
            String clientOrderId,
            String symbol,
            Side side,
            OrderType type,
            long price,
            long quantity,
            long submitTimeNanos,
            long submitTimeEpochMs) {
        this.id = requireNonBlank(id, "id");
        this.clientOrderId = requireNonBlank(clientOrderId, "clientOrderId");
        this.symbol = requireNonBlank(symbol, "symbol");
        this.side = Objects.requireNonNull(side, "side");
        this.type = Objects.requireNonNull(type, "type");

        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive: " + quantity);
        }
        if (type == OrderType.LIMIT && price <= 0) {
            throw new IllegalArgumentException("LIMIT order requires positive price: " + price);
        }
        if (type == OrderType.MARKET && price != 0) {
            throw new IllegalArgumentException("MARKET order must have price=0, got: " + price);
        }

        this.price = price;
        this.quantity = quantity;
        this.submitTimeNanos = submitTimeNanos;
        this.submitTimeEpochMs = submitTimeEpochMs;
        this.remainingQuantity = quantity;
        this.status = OrderStatus.NEW;
    }

    public String id() { return id; }
    public String clientOrderId() { return clientOrderId; }
    public String symbol() { return symbol; }
    public Side side() { return side; }
    public OrderType type() { return type; }
    public long price() { return price; }
    public long quantity() { return quantity; }
    public long submitTimeNanos() { return submitTimeNanos; }
    public long submitTimeEpochMs() { return submitTimeEpochMs; }
    public long remainingQuantity() { return remainingQuantity; }
    public OrderStatus status() { return status; }

    void setRemainingQuantity(long remaining) {
        if (remaining < 0 || remaining > quantity) {
            throw new IllegalArgumentException(
                    "remaining out of range [0, " + quantity + "]: " + remaining);
        }
        this.remainingQuantity = remaining;
    }

    void setStatus(OrderStatus status) {
        this.status = Objects.requireNonNull(status, "status");
    }

    @Override
    public String toString() {
        return "Order[id=" + id
                + ", symbol=" + symbol
                + ", side=" + side
                + ", type=" + type
                + ", price=" + price
                + ", qty=" + quantity
                + ", remaining=" + remainingQuantity
                + ", status=" + status + "]";
    }

    private static String requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " required");
        }
        return value;
    }
}
