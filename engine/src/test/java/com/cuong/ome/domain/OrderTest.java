package com.cuong.ome.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    private static Order limitBuy(long price, long qty) {
        return new Order("o-1", "c-1", "AAPL", Side.BUY, OrderType.LIMIT, price, qty, 1000L, 1L);
    }

    private static Order marketBuy(long qty) {
        return new Order("o-1", "c-1", "AAPL", Side.BUY, OrderType.MARKET, 0L, qty, 1000L, 1L);
    }

    @Test
    void should_initialise_with_NEW_status_and_full_remaining_quantity() {
        Order order = limitBuy(15_000_000L, 100L);

        assertThat(order.status()).isEqualTo(OrderStatus.NEW);
        assertThat(order.remainingQuantity()).isEqualTo(100L);
        assertThat(order.quantity()).isEqualTo(100L);
        assertThat(order.price()).isEqualTo(15_000_000L);
    }

    @Test
    void should_reject_negative_quantity_in_constructor() {
        assertThatThrownBy(() -> limitBuy(15_000_000L, -1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("quantity must be positive");
    }

    @Test
    void should_reject_zero_quantity_in_constructor() {
        assertThatThrownBy(() -> limitBuy(15_000_000L, 0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("quantity must be positive");
    }

    @Test
    void should_reject_zero_price_for_limit_order() {
        assertThatThrownBy(() -> limitBuy(0L, 100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("LIMIT order requires positive price");
    }

    @Test
    void should_reject_negative_price_for_limit_order() {
        assertThatThrownBy(() -> limitBuy(-1L, 100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("LIMIT order requires positive price");
    }

    @Test
    void should_allow_zero_price_for_market_order() {
        Order order = marketBuy(50L);

        assertThat(order.type()).isEqualTo(OrderType.MARKET);
        assertThat(order.price()).isZero();
    }

    @Test
    void should_reject_market_order_with_non_zero_price() {
        assertThatThrownBy(() -> new Order(
                "o-1", "c-1", "AAPL", Side.BUY, OrderType.MARKET, 100L, 50L, 1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("MARKET order must have price=0");
    }

    @Test
    void should_reject_blank_or_null_string_fields() {
        assertThatThrownBy(() -> new Order(
                null, "c-1", "AAPL", Side.BUY, OrderType.LIMIT, 1L, 1L, 1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id");
        assertThatThrownBy(() -> new Order(
                "o-1", "  ", "AAPL", Side.BUY, OrderType.LIMIT, 1L, 1L, 1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("clientOrderId");
        assertThatThrownBy(() -> new Order(
                "o-1", "c-1", "", Side.BUY, OrderType.LIMIT, 1L, 1L, 1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("symbol");
    }

    @Test
    void should_reject_null_enum_fields() {
        assertThatThrownBy(() -> new Order(
                "o-1", "c-1", "AAPL", null, OrderType.LIMIT, 1L, 1L, 1L, 1L))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Order(
                "o-1", "c-1", "AAPL", Side.BUY, null, 1L, 1L, 1L, 1L))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_apply_remaining_quantity_within_bounds_via_package_setter() {
        Order order = limitBuy(15_000_000L, 100L);
        order.setRemainingQuantity(40L);
        assertThat(order.remainingQuantity()).isEqualTo(40L);
    }

    @Test
    void should_reject_remaining_quantity_out_of_bounds() {
        Order order = limitBuy(15_000_000L, 100L);
        assertThatThrownBy(() -> order.setRemainingQuantity(-1L))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> order.setRemainingQuantity(101L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_update_status_via_package_setter() {
        Order order = limitBuy(15_000_000L, 100L);
        order.setStatus(OrderStatus.OPEN);
        assertThat(order.status()).isEqualTo(OrderStatus.OPEN);
    }

    @Test
    void side_opposite_should_swap_buy_and_sell() {
        assertThat(Side.BUY.opposite()).isEqualTo(Side.SELL);
        assertThat(Side.SELL.opposite()).isEqualTo(Side.BUY);
    }
}
