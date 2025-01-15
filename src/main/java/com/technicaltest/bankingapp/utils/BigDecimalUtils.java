package com.technicaltest.bankingapp.utils;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class BigDecimalUtils {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

    /**
     * Normalizes a BigDecimal to the default scale and rounding mode.
     *
     * @param value the BigDecimal to normalize
     * @return the normalized BigDecimal
     */
    public static BigDecimal normalize(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.setScale(SCALE, ROUNDING_MODE);
    }
}
