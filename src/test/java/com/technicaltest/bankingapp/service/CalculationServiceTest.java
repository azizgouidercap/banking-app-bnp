package com.technicaltest.bankingapp.service;

import com.technicaltest.bankingapp.config.Config;
import com.technicaltest.bankingapp.config.ConfigLoader;
import com.technicaltest.bankingapp.exception.InvalidOperationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;

class CalculationServiceTest {

    private final CalculationService calculationService = new CalculationService();

    @Test
    void calculateSavingsInterest_shouldCalculateSavingsInterest_whenAmountIsValid() {
        try (MockedStatic<ConfigLoader> configLoaderMock = mockStatic(ConfigLoader.class)) {
            // Given
            BigDecimal amount = BigDecimal.valueOf(1000);
            BigDecimal interestRate = BigDecimal.valueOf(5);
            BigDecimal savingsWithdrawMonthlyLimit = BigDecimal.valueOf(1000);
            configLoaderMock.when(ConfigLoader::loadConfig).thenReturn(new Config(interestRate, savingsWithdrawMonthlyLimit));

            // When
            BigDecimal result = calculationService.calculateSavingsInterest(amount);

            // Then
            assertThat(result).isEqualTo(BigDecimal.valueOf(4.17));
        }
    }

    @ParameterizedTest
    @CsvSource({"0", "-12"})
    void calculateSavingsInterest_shouldCalculateSavingsInterest_whenAmountIsNegative(BigDecimal amount) {
        try (MockedStatic<ConfigLoader> configLoaderMock = mockStatic(ConfigLoader.class)) {
            // Given
            BigDecimal interestRate = BigDecimal.valueOf(5);
            BigDecimal savingsWithdrawMonthlyLimit = BigDecimal.valueOf(1000);
            configLoaderMock.when(ConfigLoader::loadConfig).thenReturn(new Config(interestRate, savingsWithdrawMonthlyLimit));

            // When
            BigDecimal result = calculationService.calculateSavingsInterest(amount);

            // Then
            assertThat(result).isEqualTo(BigDecimal.ZERO);
        }
    }

    @Test
    void calculateSavingsInterest_shouldThrowException_whenAmountIsNull() {
        // Given When Then
        assertThatThrownBy(() -> calculationService.calculateSavingsInterest(null))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessage("Amount must not be null");
    }

    @Test
    void addAmount_shouldSucceed() {
        // Given
        BigDecimal balance = BigDecimal.valueOf(1000);
        BigDecimal amount = BigDecimal.valueOf(500);

        // When
        BigDecimal result = calculationService.addAmount(balance, amount);

        // Then
        assertThat(result).isEqualTo(BigDecimal.valueOf(1500).setScale(2, RoundingMode.HALF_EVEN));
    }

    @ParameterizedTest
    @CsvSource({"0", "-12"})
    void addAmount_shouldThrowException_whenAmountIsZeroOrNegative(BigDecimal amount) {
        // Given
        BigDecimal balance = BigDecimal.valueOf(1000);

        // When & Then
        assertThatThrownBy(() -> calculationService.addAmount(balance, amount))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessage("Amount to add must be greater than zero.");
    }

    @Test
    void subtractAmount_shouldSucceed() {
        // Given
        BigDecimal balance = BigDecimal.valueOf(1000);
        BigDecimal amount = BigDecimal.valueOf(200);

        // When
        BigDecimal result = calculationService.subtractAmount(balance, amount);

        // Then
        assertThat(result).isEqualTo(BigDecimal.valueOf(800).setScale(2, RoundingMode.HALF_EVEN));
    }

    @ParameterizedTest
    @CsvSource({"0", "-12"})
    void subtractAmount_shouldThrowException_whenAmountIsZeroOrNegative(BigDecimal amount) {
        // Given
        BigDecimal balance = BigDecimal.valueOf(1000);

        // When & Then
        assertThatThrownBy(() -> calculationService.subtractAmount(balance, amount))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessage("Amount to subtract must be greater than zero.");
    }
}

