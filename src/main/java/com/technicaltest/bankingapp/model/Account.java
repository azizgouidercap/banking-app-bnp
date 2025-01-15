package com.technicaltest.bankingapp.model;

import com.technicaltest.bankingapp.enumeration.AccountType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@SuperBuilder(toBuilder = true)
public abstract class Account {
    private Long id;
    private String ownerName;
    private BigDecimal balance;
    private Instant createdAt;
    private Instant updatedAt;

    public abstract AccountType getAccountType();
}
