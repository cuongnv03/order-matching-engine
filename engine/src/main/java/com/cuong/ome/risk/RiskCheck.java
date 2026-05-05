package com.cuong.ome.risk;

import com.cuong.ome.config.EngineConfig;
import com.cuong.ome.config.RiskLimits;
import com.cuong.ome.config.SymbolConfig;
import com.cuong.ome.domain.Order;
import com.cuong.ome.domain.OrderType;

import java.util.Optional;

public final class RiskCheck {

    private RiskCheck() {
    }

    public static Optional<RiskRejection> check(Order order, EngineConfig config) {
        SymbolConfig symbolConfig = config.symbols().get(order.symbol());
        if (symbolConfig == null) {
            return reject(RejectReason.UNKNOWN_SYMBOL, "unknown symbol: " + order.symbol());
        }

        RiskLimits limits = config.riskLimits();
        if (order.quantity() > limits.maxOrderQuantity()) {
            return reject(RejectReason.QUANTITY_EXCEEDS_MAX,
                    "qty " + order.quantity() + " > max " + limits.maxOrderQuantity());
        }

        if (order.clientOrderId().length() > limits.maxClientOrderIdLen()) {
            return reject(RejectReason.CLIENT_ORDER_ID_TOO_LONG,
                    "clientOrderId length " + order.clientOrderId().length()
                            + " > max " + limits.maxClientOrderIdLen());
        }

        if (order.type() == OrderType.LIMIT) {
            long lower = symbolConfig.lowerBand();
            long upper = symbolConfig.upperBand();
            if (order.price() < lower || order.price() > upper) {
                return reject(RejectReason.PRICE_OUTSIDE_BAND,
                        "price " + order.price() + " outside band [" + lower + ", " + upper + "]");
            }
        }

        return Optional.empty();
    }

    private static Optional<RiskRejection> reject(RejectReason reason, String message) {
        return Optional.of(new RiskRejection(reason, message));
    }
}
