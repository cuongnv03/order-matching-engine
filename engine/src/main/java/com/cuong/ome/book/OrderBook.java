package com.cuong.ome.book;

import com.cuong.ome.domain.Order;
import com.cuong.ome.domain.Side;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.TreeMap;

public final class OrderBook {

    private final String symbol;
    private final TreeMap<Long, PriceLevel> bids;
    private final TreeMap<Long, PriceLevel> asks;
    private final Map<String, Order> ordersById;
    private long sequence;

    public OrderBook(String symbol) {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("symbol required");
        }
        this.symbol = symbol;
        this.bids = new TreeMap<>(Comparator.reverseOrder());
        this.asks = new TreeMap<>();
        this.ordersById = new HashMap<>();
        this.sequence = 0L;
    }

    public void addRestingOrder(Order order) {
        Objects.requireNonNull(order, "order");
        if (!order.symbol().equals(symbol)) {
            throw new IllegalArgumentException(
                    "symbol mismatch: order=" + order.symbol() + " book=" + symbol);
        }
        if (ordersById.containsKey(order.id())) {
            throw new IllegalStateException("order already in book: " + order.id());
        }
        TreeMap<Long, PriceLevel> sideBook = sideBookFor(order.side());
        PriceLevel level = sideBook.computeIfAbsent(order.price(), PriceLevel::new);
        level.appendOrder(order);
        ordersById.put(order.id(), order);
        sequence++;
    }

    public Optional<Order> cancel(String orderId) {
        Objects.requireNonNull(orderId, "orderId");
        Order order = ordersById.remove(orderId);
        if (order == null) {
            return Optional.empty();
        }
        TreeMap<Long, PriceLevel> sideBook = sideBookFor(order.side());
        PriceLevel level = sideBook.get(order.price());
        if (level != null) {
            level.removeOrder(order);
            if (level.isEmpty()) {
                sideBook.remove(order.price());
            }
        }
        sequence++;
        return Optional.of(order);
    }

    public OptionalLong bestBid() {
        return bids.isEmpty() ? OptionalLong.empty() : OptionalLong.of(bids.firstKey());
    }

    public OptionalLong bestAsk() {
        return asks.isEmpty() ? OptionalLong.empty() : OptionalLong.of(asks.firstKey());
    }

    public BookSnapshot snapshot(int depth, long timestampEpochMs) {
        if (depth <= 0) {
            throw new IllegalArgumentException("depth must be positive: " + depth);
        }
        return new BookSnapshot(
                symbol,
                topLevels(bids, depth),
                topLevels(asks, depth),
                timestampEpochMs,
                sequence);
    }

    public String symbol() { return symbol; }
    public long sequence() { return sequence; }
    public int restingOrderCount() { return ordersById.size(); }

    TreeMap<Long, PriceLevel> bids() { return bids; }
    TreeMap<Long, PriceLevel> asks() { return asks; }
    Map<String, Order> ordersById() { return ordersById; }

    private TreeMap<Long, PriceLevel> sideBookFor(Side side) {
        return side == Side.BUY ? bids : asks;
    }

    private static List<BookLevel> topLevels(TreeMap<Long, PriceLevel> sideBook, int depth) {
        List<BookLevel> out = new ArrayList<>(Math.min(depth, sideBook.size()));
        for (PriceLevel level : sideBook.values()) {
            if (out.size() >= depth) break;
            out.add(new BookLevel(level.price(), level.totalQuantity(), level.orderCount()));
        }
        return out;
    }
}
