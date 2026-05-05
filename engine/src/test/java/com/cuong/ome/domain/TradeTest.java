package com.cuong.ome.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TradeTest {

    @Test
    void should_construct_valid_trade() {
        Trade trade = new Trade("t-1", "AAPL", "buy-1", "sell-1", 15_000_000L, 100L, 1730000000000L);

        assertThat(trade.id()).isEqualTo("t-1");
        assertThat(trade.price()).isEqualTo(15_000_000L);
        assertThat(trade.quantity()).isEqualTo(100L);
    }

    @Test
    void should_reject_self_trade() {
        assertThatThrownBy(() -> new Trade("t-1", "AAPL", "same", "same", 1L, 1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("self-trade");
    }

    @Test
    void should_reject_non_positive_price_or_quantity() {
        assertThatThrownBy(() -> new Trade("t-1", "AAPL", "b", "s", 0L, 1L, 1L))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Trade("t-1", "AAPL", "b", "s", 1L, 0L, 1L))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Trade("t-1", "AAPL", "b", "s", -1L, 1L, 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_reject_blank_fields() {
        assertThatThrownBy(() -> new Trade("", "AAPL", "b", "s", 1L, 1L, 1L))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Trade("t-1", " ", "b", "s", 1L, 1L, 1L))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Trade("t-1", "AAPL", null, "s", 1L, 1L, 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void records_should_have_value_equality() {
        Trade a = new Trade("t-1", "AAPL", "b", "s", 1L, 1L, 100L);
        Trade b = new Trade("t-1", "AAPL", "b", "s", 1L, 1L, 100L);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }
}
