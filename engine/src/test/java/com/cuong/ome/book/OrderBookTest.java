package com.cuong.ome.book;

import com.cuong.ome.domain.Order;
import com.cuong.ome.domain.OrderType;
import com.cuong.ome.domain.Side;

import java.util.Optional;
import java.util.OptionalLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderBookTest {

    private OrderBook book;
    private long nano;

    @BeforeEach
    void setUp() {
        book = new OrderBook("AAPL");
        nano = 0L;
    }

    private Order limit(String id, Side side, long price, long qty) {
        return new Order(id, "c-" + id, "AAPL", side, OrderType.LIMIT, price, qty, ++nano, 1L);
    }

    @Test
    void should_add_resting_buy_to_bids_side() {
        book.addRestingOrder(limit("o-1", Side.BUY, 10_000L, 100L));

        assertThat(book.bestBid()).isEqualTo(OptionalLong.of(10_000L));
        assertThat(book.bestAsk()).isEmpty();
        assertThat(book.restingOrderCount()).isEqualTo(1);
    }

    @Test
    void should_add_resting_sell_to_asks_side() {
        book.addRestingOrder(limit("o-1", Side.SELL, 10_500L, 50L));

        assertThat(book.bestAsk()).isEqualTo(OptionalLong.of(10_500L));
        assertThat(book.bestBid()).isEmpty();
    }

    @Test
    void bestBid_should_be_highest_among_multiple_bids() {
        book.addRestingOrder(limit("o-1", Side.BUY, 9_900L, 10L));
        book.addRestingOrder(limit("o-2", Side.BUY, 10_000L, 10L));
        book.addRestingOrder(limit("o-3", Side.BUY, 9_800L, 10L));

        assertThat(book.bestBid()).isEqualTo(OptionalLong.of(10_000L));
    }

    @Test
    void bestAsk_should_be_lowest_among_multiple_asks() {
        book.addRestingOrder(limit("o-1", Side.SELL, 10_500L, 10L));
        book.addRestingOrder(limit("o-2", Side.SELL, 10_400L, 10L));
        book.addRestingOrder(limit("o-3", Side.SELL, 10_600L, 10L));

        assertThat(book.bestAsk()).isEqualTo(OptionalLong.of(10_400L));
    }

    @Test
    void should_keep_FIFO_within_price_level() {
        Order first = limit("o-1", Side.BUY, 10_000L, 100L);
        Order second = limit("o-2", Side.BUY, 10_000L, 50L);
        Order third = limit("o-3", Side.BUY, 10_000L, 25L);

        book.addRestingOrder(first);
        book.addRestingOrder(second);
        book.addRestingOrder(third);

        PriceLevel level = book.bids().get(10_000L);
        assertThat(level.orders()).containsExactly(first, second, third);
    }

    @Test
    void should_track_totalQty_per_level_correctly() {
        book.addRestingOrder(limit("o-1", Side.BUY, 10_000L, 100L));
        book.addRestingOrder(limit("o-2", Side.BUY, 10_000L, 50L));
        book.addRestingOrder(limit("o-3", Side.BUY, 9_900L, 30L));

        assertThat(book.bids().get(10_000L).totalQuantity()).isEqualTo(150L);
        assertThat(book.bids().get(9_900L).totalQuantity()).isEqualTo(30L);
    }

    @Test
    void should_reject_order_with_existing_id() {
        book.addRestingOrder(limit("o-dup", Side.BUY, 10_000L, 100L));

        assertThatThrownBy(() -> book.addRestingOrder(limit("o-dup", Side.SELL, 11_000L, 1L)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already in book");
    }

    @Test
    void should_reject_order_with_mismatched_symbol() {
        Order foreign = new Order("o-x", "c-x", "MSFT", Side.BUY, OrderType.LIMIT,
                10_000L, 1L, 1L, 1L);

        assertThatThrownBy(() -> book.addRestingOrder(foreign))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("symbol mismatch");
    }

    @Test
    void cancel_should_return_canceled_order_and_remove_from_byId() {
        Order order = limit("o-1", Side.BUY, 10_000L, 100L);
        book.addRestingOrder(order);

        Optional<Order> cancelled = book.cancel("o-1");

        assertThat(cancelled).contains(order);
        assertThat(book.restingOrderCount()).isZero();
    }

    @Test
    void cancel_should_decrement_level_totalQty() {
        book.addRestingOrder(limit("o-1", Side.BUY, 10_000L, 100L));
        book.addRestingOrder(limit("o-2", Side.BUY, 10_000L, 50L));

        book.cancel("o-1");

        assertThat(book.bids().get(10_000L).totalQuantity()).isEqualTo(50L);
        assertThat(book.bids().get(10_000L).orderCount()).isEqualTo(1);
    }

    @Test
    void cancel_should_remove_empty_level_from_TreeMap() {
        book.addRestingOrder(limit("o-1", Side.BUY, 10_000L, 100L));
        assertThat(book.bids()).containsKey(10_000L);

        book.cancel("o-1");

        assertThat(book.bids()).doesNotContainKey(10_000L);
        assertThat(book.bestBid()).isEmpty();
    }

    @Test
    void cancel_should_keep_other_levels_intact() {
        book.addRestingOrder(limit("o-1", Side.BUY, 10_000L, 100L));
        book.addRestingOrder(limit("o-2", Side.BUY, 9_900L, 50L));

        book.cancel("o-1");

        assertThat(book.bestBid()).isEqualTo(OptionalLong.of(9_900L));
        assertThat(book.bids()).containsOnlyKeys(9_900L);
    }

    @Test
    void cancel_should_return_empty_when_id_unknown() {
        assertThat(book.cancel("nonexistent")).isEmpty();
    }

    @Test
    void snapshot_should_return_descending_bids_and_ascending_asks() {
        book.addRestingOrder(limit("b1", Side.BUY, 9_900L, 10L));
        book.addRestingOrder(limit("b2", Side.BUY, 10_000L, 20L));
        book.addRestingOrder(limit("b3", Side.BUY, 9_800L, 30L));
        book.addRestingOrder(limit("a1", Side.SELL, 10_500L, 40L));
        book.addRestingOrder(limit("a2", Side.SELL, 10_400L, 50L));
        book.addRestingOrder(limit("a3", Side.SELL, 10_600L, 60L));

        BookSnapshot snap = book.snapshot(10, 1730_000L);

        assertThat(snap.bids()).extracting(BookLevel::price)
                .containsExactly(10_000L, 9_900L, 9_800L);
        assertThat(snap.asks()).extracting(BookLevel::price)
                .containsExactly(10_400L, 10_500L, 10_600L);
    }

    @Test
    void snapshot_should_limit_to_top_N_levels_each_side() {
        for (int i = 0; i < 30; i++) {
            book.addRestingOrder(limit("b" + i, Side.BUY, 10_000L - i, 1L));
            book.addRestingOrder(limit("a" + i, Side.SELL, 11_000L + i, 1L));
        }

        BookSnapshot snap = book.snapshot(20, 1L);

        assertThat(snap.bids()).hasSize(20);
        assertThat(snap.asks()).hasSize(20);
        assertThat(snap.bids().get(0).price()).isEqualTo(10_000L);
        assertThat(snap.asks().get(0).price()).isEqualTo(11_000L);
    }

    @Test
    void snapshot_should_aggregate_orders_per_level() {
        book.addRestingOrder(limit("o-1", Side.BUY, 10_000L, 100L));
        book.addRestingOrder(limit("o-2", Side.BUY, 10_000L, 50L));
        book.addRestingOrder(limit("o-3", Side.BUY, 9_900L, 30L));

        BookSnapshot snap = book.snapshot(10, 1L);

        BookLevel top = snap.bids().get(0);
        assertThat(top.price()).isEqualTo(10_000L);
        assertThat(top.totalQuantity()).isEqualTo(150L);
        assertThat(top.orderCount()).isEqualTo(2);
    }

    @Test
    void sequence_should_increment_on_add_and_cancel_only() {
        assertThat(book.sequence()).isZero();

        book.addRestingOrder(limit("o-1", Side.BUY, 10_000L, 100L));
        long afterAdd = book.sequence();
        assertThat(afterAdd).isEqualTo(1L);

        book.snapshot(5, 1L);
        assertThat(book.sequence()).isEqualTo(afterAdd);

        book.cancel("o-1");
        assertThat(book.sequence()).isEqualTo(2L);
    }

    @Test
    void snapshot_should_carry_current_sequence() {
        book.addRestingOrder(limit("o-1", Side.BUY, 10_000L, 1L));
        book.addRestingOrder(limit("o-2", Side.SELL, 11_000L, 1L));

        BookSnapshot snap = book.snapshot(5, 1730_000L);

        assertThat(snap.sequence()).isEqualTo(2L);
        assertThat(snap.timestampEpochMs()).isEqualTo(1730_000L);
        assertThat(snap.symbol()).isEqualTo("AAPL");
    }
}
