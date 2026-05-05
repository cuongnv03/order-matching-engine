package com.cuong.ome.util;

import java.math.BigDecimal;
import java.util.Objects;

public final class PriceUtil {

    public static final int SCALE = 4;
    public static final long MULTIPLIER = 10_000L;

    private PriceUtil() {
    }

    public static long toLong(BigDecimal decimal) {
        Objects.requireNonNull(decimal, "decimal");
        if (decimal.scale() > SCALE) {
            throw new IllegalArgumentException(
                    "price has more than " + SCALE + " decimal places: " + decimal.toPlainString());
        }
        return decimal.movePointRight(SCALE).longValueExact();
    }

    public static BigDecimal toDecimal(long value) {
        return BigDecimal.valueOf(value, SCALE);
    }

    public static String toDecimalString(long value) {
        return toDecimal(value).toPlainString();
    }

    public static long parse(String s) {
        Objects.requireNonNull(s, "s");
        return toLong(new BigDecimal(s.trim()));
    }
}
