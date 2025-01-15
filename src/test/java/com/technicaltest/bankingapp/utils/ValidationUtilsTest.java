package com.technicaltest.bankingapp.utils;

import com.technicaltest.bankingapp.exception.InvalidOperationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilsTest {

    @Test
    void requireNonNull_shouldNotThrow_whenValueIsNotNull() {
        // Given
        Object value = "test";

        // When & Then
        assertDoesNotThrow(() -> ValidationUtils.requireNonNull(value, "TestField"));
    }

    @Test
    void requireNonNull_shouldThrow_whenValueIsNull() {
        // Given
        Object value = null;

        // When & Then
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> ValidationUtils.requireNonNull(value, "TestField")
        );
        assertThat(exception.getMessage()).isEqualTo("TestField must not be null.");
    }

    @Test
    void requirePositiveNumber_shouldNotThrow_whenBigDecimalIsPositive() {
        // Given
        BigDecimal value = BigDecimal.valueOf(10);

        // When & Then
        assertDoesNotThrow(() -> ValidationUtils.requirePositiveNumber(value, "Amount"));
    }

    @Test
    void requirePositiveNumber_shouldThrow_whenBigDecimalIsZero() {
        // Given
        BigDecimal value = BigDecimal.ZERO;

        // When & Then
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> ValidationUtils.requirePositiveNumber(value, "Amount")
        );
        assertThat(exception.getMessage()).isEqualTo("Amount must be greater than zero.");
    }

    @Test
    void requirePositiveNumber_shouldThrow_whenBigDecimalIsNegative() {
        // Given
        BigDecimal value = BigDecimal.valueOf(-10);

        // When & Then
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> ValidationUtils.requirePositiveNumber(value, "Amount")
        );
        assertThat(exception.getMessage()).isEqualTo("Amount must be greater than zero.");
    }

    @Test
    void requirePositiveNumber_shouldNotThrow_whenIntIsPositive() {
        // Given
        int value = 10;

        // When & Then
        assertDoesNotThrow(() -> ValidationUtils.requirePositiveNumber(value, "TestField"));
    }

    @Test
    void requirePositiveNumber_shouldThrow_whenIntIsZero() {
        // Given
        int value = 0;

        // When & Then
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> ValidationUtils.requirePositiveNumber(value, "TestField")
        );
        assertThat(exception.getMessage()).isEqualTo("TestField must be greater than zero.");
    }

    @Test
    void requirePositiveNumber_shouldThrow_whenIntIsNegative() {
        // Given
        int value = -10;

        // When & Then
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> ValidationUtils.requirePositiveNumber(value, "TestField")
        );
        assertThat(exception.getMessage()).isEqualTo("TestField must be greater than zero.");
    }

    @Test
    void requirePositiveNumber_shouldNotThrow_whenLongIsPositive() {
        // Given
        long value = 100L;

        // When & Then
        assertDoesNotThrow(() -> ValidationUtils.requirePositiveNumber(value, "TestField"));
    }

    @Test
    void requirePositiveNumber_shouldThrow_whenLongIsZero() {
        // Given
        long value = 0L;

        // When & Then
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> ValidationUtils.requirePositiveNumber(value, "TestField")
        );
        assertThat(exception.getMessage()).isEqualTo("TestField must be greater than zero.");
    }

    @Test
    void requirePositiveNumber_shouldThrow_whenLongIsNegative() {
        // Given
        long value = -100L;

        // When & Then
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> ValidationUtils.requirePositiveNumber(value, "TestField")
        );
        assertThat(exception.getMessage()).isEqualTo("TestField must be greater than zero.");
    }
}
