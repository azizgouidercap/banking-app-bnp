package com.technicaltest.bankingapp.dto;

import com.technicaltest.bankingapp.enumeration.AccountType;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class AccountDTO {

    Long id;
    String ownerName;
    BigDecimal balance;
    AccountType accountType;
}
