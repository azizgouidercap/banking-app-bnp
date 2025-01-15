package com.technicaltest.bankingapp.service;

import com.technicaltest.bankingapp.exception.InvalidOperationException;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.technicaltest.bankingapp.config.ConfigLoader.loadConfig;
import static com.technicaltest.bankingapp.utils.BigDecimalUtils.normalize;

public class CalculationService {

    private static final BigDecimal INTEREST_RATE = loadConfig().savingsInterestRate();

    public BigDecimal calculateSavingsInterest(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidOperationException("Amount must not be null");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0 || INTEREST_RATE.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // Precompute the monthly interest rate (annual rate divided by 12)
        BigDecimal monthlyInterestRate = INTEREST_RATE.divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_UP);

        // Calculate the interest and round to 2 decimal places
        return normalize(amount.multiply(monthlyInterestRate));
    }

    public BigDecimal addAmount(BigDecimal balance, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOperationException("Amount to add must be greater than zero.");
        }

        return normalize(balance.add(amount));
    }

    public BigDecimal subtractAmount(BigDecimal balance, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOperationException("Amount to subtract must be greater than zero.");
        }

        return normalize(balance.subtract(amount));

    }
}
