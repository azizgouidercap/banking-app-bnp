package com.technicaltest.bankingapp.config;

import java.math.BigDecimal;

public record Config(BigDecimal savingsInterestRate, BigDecimal savingsWithdrawMonthlyLimit) {}
