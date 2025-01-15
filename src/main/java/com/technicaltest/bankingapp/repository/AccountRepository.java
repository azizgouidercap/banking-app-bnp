package com.technicaltest.bankingapp.repository;

import com.technicaltest.bankingapp.model.Account;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AccountRepository {

    private final Map<Long, Account> accountStorage = new HashMap<>();
    private long nextId = 1;

    public <T extends Account> T save(T account) {
        if (account.getId() == null) {
            account.setId(nextId++);
            account.setCreatedAt(Instant.now());
        }
        account.setUpdatedAt(Instant.now());
        accountStorage.put(account.getId(), account);
        return account;
    }

    public Optional<Account> findById(Long id) {
        return Optional.ofNullable(accountStorage.get(id));
    }
}
