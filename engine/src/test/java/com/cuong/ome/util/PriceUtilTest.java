package com.cuong.ome.util;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PriceUtilTest {

    @Test
    void should_round_trip_decimal_to_long_and_back() {
        BigDecimal decimal = new BigDecimal("1500.5");
        long encoded = PriceUtil.toLong(decimal);

        assertThat(encoded).isEqualTo(15_005_000L);
        assertThat(PriceUtil.toDecimal(encoded)).isEqualByComparingTo(decimal);
    }

    @Test
    void should_round_trip_full_4_decimal_precision() {
        BigDecimal decimal = new BigDecimal("0.0001");
        long encoded = PriceUtil.toLong(decimal);

        assertThat(encoded).isEqualTo(1L);
        assertThat(PriceUtil.toDecimalString(encoded)).isEqualTo("0.0001");
    }

    @Test
    void should_reject_more_than_4_decimal_places() {
        assertThatThrownBy(() -> PriceUtil.toLong(new BigDecimal("1.23456")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("more than 4");
    }

    @Test
    void should_handle_zero_and_negative_values() {
        assertThat(PriceUtil.toLong(BigDecimal.ZERO)).isZero();
        assertThat(PriceUtil.toLong(new BigDecimal("-12.34"))).isEqualTo(-123_400L);
        assertThat(PriceUtil.toDecimalString(-123_400L)).isEqualTo("-12.3400");
    }

    @Test
    void should_parse_string_to_long() {
        assertThat(PriceUtil.parse("99.9999")).isEqualTo(999_999L);
        assertThat(PriceUtil.parse("  100  ")).isEqualTo(1_000_000L);
    }

    @Test
    void should_format_long_to_canonical_decimal_string() {
        assertThat(PriceUtil.toDecimalString(15_000_000L)).isEqualTo("1500.0000");
        assertThat(PriceUtil.toDecimalString(0L)).isEqualTo("0.0000");
    }

    @Test
    void should_reject_null_input() {
        assertThatThrownBy(() -> PriceUtil.toLong(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> PriceUtil.parse(null))
                .isInstanceOf(NullPointerException.class);
    }
}
