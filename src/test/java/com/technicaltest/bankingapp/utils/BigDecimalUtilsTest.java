package com.technicaltest.bankingapp.utils;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BigDecimalUtilsTest {

    @Test
    void normalize_shouldThrowException_whenValueIsNull() {
        // Given
        BigDecimal value = null;

        // When
        BigDecimal result = BigDecimalUtils.normalize(value);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void normalize_shouldReturnSameValue_whenAlreadyNormalized() {
        // Given
        BigDecimal value = new BigDecimal("123.45");

        // When
        BigDecimal result = BigDecimalUtils.normalize(value);

        // Then
        assertThat(result).isEqualTo(new BigDecimal("123.45"));
    }

    @Test
    void normalize_shouldAdjustScale_whenScaleIsTooLow() {
        // Given
        BigDecimal value = new BigDecimal("123");

        // When
        BigDecimal result = BigDecimalUtils.normalize(value);

        // Then
        assertThat(result).isEqualTo(new BigDecimal("123.00"));
    }

    @Test
    void normalize_shouldRoundProperly_whenScaleIsTooHigh() {
        // Given
        BigDecimal value = new BigDecimal("123.456");

        // When
        BigDecimal result = BigDecimalUtils.normalize(value);

        // Then
        assertThat(result).isEqualTo(new BigDecimal("123.46"));
    }

    @Test
    void normalize_shouldRoundDown_whenRoundingIsNeeded() {
        // Given
        BigDecimal value = new BigDecimal("123.444");

        // When
        BigDecimal result = BigDecimalUtils.normalize(value);

        // Then
        assertThat(result).isEqualTo(new BigDecimal("123.44"));
    }
}
