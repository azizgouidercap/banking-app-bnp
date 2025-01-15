package com.technicaltest.bankingapp.builder;

import com.technicaltest.bankingapp.model.CheckingAccount;
import com.technicaltest.bankingapp.model.SavingsAccount;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.Instant;

@UtilityClass
public class AccountBuilderFactory {

    public static final String OWNER_NAME = "John Doe";
    public static final Long DEFAULT_ID = 1L;

    public static SavingsAccount buildSavingsAccount(BigDecimal balance) {
        return buildSavingsAccount(balance, balance);
    }

    public static SavingsAccount buildSavingsAccount(BigDecimal balance, BigDecimal monthlyInterestBase) {
        return SavingsAccount
                .builder()
                .ownerName(OWNER_NAME)
                .id(DEFAULT_ID)
                .balance(balance)
                .monthlyInterestBase(monthlyInterestBase)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public static CheckingAccount buildCheckingAccount(BigDecimal balance) {
        return CheckingAccount
                .builder()
                .ownerName(OWNER_NAME)
                .id(DEFAULT_ID)
                .balance(balance)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}
