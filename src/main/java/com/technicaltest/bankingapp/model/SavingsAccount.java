package com.technicaltest.bankingapp.model;

import com.technicaltest.bankingapp.enumeration.AccountType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class SavingsAccount extends Account {

    private BigDecimal monthlyInterestBase;

    @Override
    public AccountType getAccountType() {
        return AccountType.SAVINGS;
    }
}
