package com.cuong.ome.risk;

import com.cuong.ome.config.EngineConfig;
import com.cuong.ome.config.RiskLimits;
import com.cuong.ome.config.SymbolConfig;
import com.cuong.ome.domain.Order;
import com.cuong.ome.domain.OrderType;
import com.cuong.ome.domain.Side;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class RiskCheckTest {

    private static final SymbolConfig AAPL = new SymbolConfig(10_000L, 700);
    private static final RiskLimits LIMITS = new RiskLimits(10_000L, 64);

    private static EngineConfig config() {
        Map<String, SymbolConfig> symbols = new HashMap<>();
        symbols.put("AAPL", AAPL);
        return new EngineConfig(symbols, LIMITS);
    }

    private static Order limit(String symbol, long price, long qty) {
        return new Order("o-1", "c-1", symbol, Side.BUY, OrderType.LIMIT, price, qty, 1L, 1L);
    }

    private static Order market(String symbol, long qty) {
        return new Order("o-1", "c-1", symbol, Side.BUY, OrderType.MARKET, 0L, qty, 1L, 1L);
    }

    private static Order limitWithClientId(String clientId) {
        return new Order("o-1", clientId, "AAPL", Side.BUY, OrderType.LIMIT, 10_000L, 100L, 1L, 1L);
    }

    @Test
    void should_pass_valid_limit_order() {
        Optional<RiskRejection> result = RiskCheck.check(limit("AAPL", 10_000L, 100L), config());
        assertThat(result).isEmpty();
    }

    @Test
    void should_pass_valid_market_order_skipping_band_check() {
        Optional<RiskRejection> result = RiskCheck.check(market("AAPL", 100L), config());
        assertThat(result).isEmpty();
    }

    @Test
    void should_reject_unknown_symbol_first() {
        Optional<RiskRejection> result = RiskCheck.check(limit("UNKNOWN", 10_000L, 100L), config());

        assertThat(result).isPresent();
        assertThat(result.get().reason()).isEqualTo(RejectReason.UNKNOWN_SYMBOL);
        assertThat(result.get().message()).contains("UNKNOWN");
    }

    @Test
    void should_reject_when_quantity_exceeds_max() {
        Optional<RiskRejection> result = RiskCheck.check(limit("AAPL", 10_000L, 10_001L), config());

        assertThat(result).isPresent();
        assertThat(result.get().reason()).isEqualTo(RejectReason.QUANTITY_EXCEEDS_MAX);
    }

    @Test
    void should_pass_when_quantity_equals_max() {
        Optional<RiskRejection> result = RiskCheck.check(limit("AAPL", 10_000L, 10_000L), config());
        assertThat(result).isEmpty();
    }

    @ParameterizedTest(name = "price {0} → reject={1}")
    @CsvSource({
            "9298,  true",
            "9299,  true",
            "9300,  false",
            "10000, false",
            "10700, false",
            "10701, true",
            "10702, true"
    })
    void should_apply_price_band_inclusive_at_boundaries(long price, boolean shouldReject) {
        Optional<RiskRejection> result = RiskCheck.check(limit("AAPL", price, 100L), config());

        if (shouldReject) {
            assertThat(result).isPresent();
            assertThat(result.get().reason()).isEqualTo(RejectReason.PRICE_OUTSIDE_BAND);
        } else {
            assertThat(result).isEmpty();
        }
    }

    @Test
    void should_skip_band_check_for_market_orders() {
        SymbolConfig narrowBand = new SymbolConfig(10_000L, 1);
        Map<String, SymbolConfig> symbols = new HashMap<>();
        symbols.put("AAPL", narrowBand);
        EngineConfig narrowConfig = new EngineConfig(symbols, LIMITS);

        Optional<RiskRejection> result = RiskCheck.check(market("AAPL", 100L), narrowConfig);
        assertThat(result).isEmpty();
    }

    @Test
    void should_reject_client_order_id_longer_than_max() {
        String tooLong = "x".repeat(65);
        Optional<RiskRejection> result = RiskCheck.check(limitWithClientId(tooLong), config());

        assertThat(result).isPresent();
        assertThat(result.get().reason()).isEqualTo(RejectReason.CLIENT_ORDER_ID_TOO_LONG);
    }

    @Test
    void should_pass_client_order_id_exactly_at_max() {
        String maxLen = "x".repeat(64);
        Optional<RiskRejection> result = RiskCheck.check(limitWithClientId(maxLen), config());
        assertThat(result).isEmpty();
    }
}
