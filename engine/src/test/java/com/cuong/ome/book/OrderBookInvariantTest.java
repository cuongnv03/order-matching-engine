package com.cuong.ome.book;

import com.cuong.ome.domain.Order;
import com.cuong.ome.domain.OrderType;
import com.cuong.ome.domain.Side;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.RepeatedTest;

import static org.assertj.core.api.Assertions.assertThat;

class OrderBookInvariantTest {

    private static final int OPS_PER_RUN = 5_000;
    private static final long[] PRICES = {9_800L, 9_900L, 10_000L, 10_100L, 10_200L};

    @RepeatedTest(10)
    void random_add_cancel_sequence_preserves_invariants() {
        OrderBook book = new OrderBook("AAPL");
        Random random = new Random();
        List<String> liveIds = new ArrayList<>();
        long nano = 0L;

        for (int i = 0; i < OPS_PER_RUN; i++) {
            boolean addOp = liveIds.isEmpty() || random.nextDouble() < 0.6;

            if (addOp) {
                String id = "o-" + i;
                long price = PRICES[random.nextInt(PRICES.length)];
                long qty = 1L + random.nextInt(100);
                Side side = price < 10_000L ? Side.BUY : Side.SELL;
                book.addRestingOrder(new Order(
                        id, "c-" + id, "AAPL", side, OrderType.LIMIT, price, qty, ++nano, 1L));
                liveIds.add(id);
            } else {
                String victim = liveIds.remove(random.nextInt(liveIds.size()));
                assertThat(book.cancel(victim)).isPresent();
            }

            assertInvariants(book);
        }
    }

    private static void assertInvariants(OrderBook book) {
        long bidsLevelTotal = book.bids().values().stream()
                .mapToLong(PriceLevel::totalQuantity).sum();
        long bidsOrderTotal = book.ordersById().values().stream()
                .filter(o -> o.side() == Side.BUY)
                .mapToLong(Order::remainingQuantity).sum();

        long asksLevelTotal = book.asks().values().stream()
                .mapToLong(PriceLevel::totalQuantity).sum();
        long asksOrderTotal = book.ordersById().values().stream()
                .filter(o -> o.side() == Side.SELL)
                .mapToLong(Order::remainingQuantity).sum();

        assertThat(bidsLevelTotal)
                .as("sum(bid level.totalQty) must equal sum(bid order.remainingQty)")
                .isEqualTo(bidsOrderTotal);
        assertThat(asksLevelTotal)
                .as("sum(ask level.totalQty) must equal sum(ask order.remainingQty)")
                .isEqualTo(asksOrderTotal);

        for (PriceLevel level : book.bids().values()) {
            assertThat(level.isEmpty())
                    .as("no empty levels remain in bids TreeMap")
                    .isFalse();
        }
        for (PriceLevel level : book.asks().values()) {
            assertThat(level.isEmpty())
                    .as("no empty levels remain in asks TreeMap")
                    .isFalse();
        }
    }
}
