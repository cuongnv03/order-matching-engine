package com.cuong.ome.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SymbolConfigTest {

    @Test
    void should_compute_band_at_7_percent_around_ref_price() {
        SymbolConfig sc = new SymbolConfig(10_000L, 700);

        assertThat(sc.lowerBand()).isEqualTo(9_300L);
        assertThat(sc.upperBand()).isEqualTo(10_700L);
    }

    @Test
    void zero_band_should_collapse_to_ref_price() {
        SymbolConfig sc = new SymbolConfig(10_000L, 0);

        assertThat(sc.lowerBand()).isEqualTo(10_000L);
        assertThat(sc.upperBand()).isEqualTo(10_000L);
    }

    @Test
    void should_reject_non_positive_ref_price() {
        assertThatThrownBy(() -> new SymbolConfig(0L, 700))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("refPrice");
        assertThatThrownBy(() -> new SymbolConfig(-1L, 700))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_reject_band_outside_0_to_10000_bps() {
        assertThatThrownBy(() -> new SymbolConfig(10_000L, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("bandBps");
        assertThatThrownBy(() -> new SymbolConfig(10_000L, 10_001))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("bandBps");
    }

    @Test
    void should_detect_overflow_in_upper_band_calculation() {
        SymbolConfig sc = new SymbolConfig(Long.MAX_VALUE / 1_000L, 700);

        assertThatThrownBy(sc::upperBand)
                .isInstanceOf(ArithmeticException.class);
    }
}
