package com.technicaltest.bankingapp.utils;

import com.technicaltest.bankingapp.exception.InvalidOperationException;

import java.math.BigDecimal;

public class ValidationUtils {

    public static void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new InvalidOperationException(fieldName + " must not be null.");
        }
    }

    public static void requirePositiveNumber(BigDecimal value, String fieldName) {
        requireNonNull(value, fieldName);
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOperationException(fieldName + " must be greater than zero.");
        }
    }

    public static void requirePositiveNumber(int value, String fieldName) {
        if (value <= 0) {
            throw new InvalidOperationException(fieldName + " must be greater than zero.");
        }
    }

    public static void requirePositiveNumber(long value, String fieldName) {
        if (value <= 0) {
            throw new InvalidOperationException(fieldName + " must be greater than zero.");
        }
    }
}
