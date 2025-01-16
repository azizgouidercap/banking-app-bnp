package com.technicaltest.bankingapp.service;

import com.technicaltest.bankingapp.dto.AccountDTO;
import com.technicaltest.bankingapp.exception.InvalidOperationException;
import com.technicaltest.bankingapp.exception.ResourceNotFoundException;
import com.technicaltest.bankingapp.mapper.AccountMapper;
import com.technicaltest.bankingapp.model.Account;
import com.technicaltest.bankingapp.model.CheckingAccount;
import com.technicaltest.bankingapp.model.SavingsAccount;
import com.technicaltest.bankingapp.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

import static com.technicaltest.bankingapp.utils.BigDecimalUtils.normalize;

@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService() {
        this.accountRepository = new AccountRepository();
    }

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountDTO createAccount(String ownerName, BigDecimal balance, int accountType) {
        log.debug("AccountService - Attempting to create account.");
        Account account;
        if (accountType == 1) {
            account = CheckingAccount.builder()
                    .balance(normalize(balance))
                    .ownerName(ownerName)
                    .build();
        } else if (accountType == 2) {
            account = SavingsAccount.builder()
                    .monthlyInterestBase(normalize(balance))
                    .balance(normalize(balance))
                    .ownerName(ownerName)
                    .build();
        } else {
            throw new InvalidOperationException("Invalid account type.");
        }

        Account createdAccount = accountRepository.save(account);
        log.debug("AccountService - Account created successfully.");
        return AccountMapper.toDTO(createdAccount);
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }

    public Account findById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
    }

}
