package com.technicaltest.bankingapp.model;

import com.technicaltest.bankingapp.enumeration.AccountType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public abstract class Account extends Entity {
    private String ownerName;
    private BigDecimal balance;

    public abstract AccountType getAccountType();
}
