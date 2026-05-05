package com.cuong.ome.config;

import java.util.Map;
import java.util.Objects;

public record EngineConfig(Map<String, SymbolConfig> symbols, RiskLimits riskLimits) {

    public EngineConfig {
        Objects.requireNonNull(symbols, "symbols");
        Objects.requireNonNull(riskLimits, "riskLimits");
        if (symbols.isEmpty()) {
            throw new IllegalArgumentException("at least one symbol must be configured");
        }
        symbols = Map.copyOf(symbols);
    }
}
