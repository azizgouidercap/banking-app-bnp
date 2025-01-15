package com.technicaltest.bankingapp.mapper;

import com.technicaltest.bankingapp.dto.AccountDTO;
import lombok.experimental.UtilityClass;
import com.technicaltest.bankingapp.model.Account;

@UtilityClass
public class AccountMapper {

    public static AccountDTO toDTO(Account account) {
        if (account == null) {
            return null;
        }

        return AccountDTO
                .builder()
                .id(account.getId())
                .balance(account.getBalance())
                .ownerName(account.getOwnerName())
                .accountType(account.getAccountType())
                .build();
    }
}
