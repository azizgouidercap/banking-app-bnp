package com.technicaltest.bankingapp.repository;

import com.technicaltest.bankingapp.model.Account;
import com.technicaltest.bankingapp.model.CheckingAccount;
import com.technicaltest.bankingapp.model.SavingsAccount;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static com.technicaltest.bankingapp.builder.AccountBuilderFactory.buildCheckingAccount;
import static com.technicaltest.bankingapp.builder.AccountBuilderFactory.buildSavingsAccount;
import static org.assertj.core.api.Assertions.assertThat;

class AccountRepositoryTest {

    private final AccountRepository accountRepository = new AccountRepository();

    @Test
    void save_shouldSaveNewSavingsAccount() {
        // Given
        SavingsAccount account = buildSavingsAccount(BigDecimal.TEN).toBuilder()
                .id(null).createdAt(null).updatedAt(null)
                .build();

        // When
        SavingsAccount savedAccount = (SavingsAccount) accountRepository.save(account);

        // Then
        assertThat(savedAccount.getId()).isNotNull();
        assertThat(savedAccount.getCreatedAt()).isNotNull();
        assertThat(savedAccount.getUpdatedAt()).isNotNull();
        assertThat(savedAccount.getOwnerName()).isEqualTo("John Doe");
        assertThat(savedAccount.getBalance()).isEqualTo(BigDecimal.TEN);
        assertThat(savedAccount.getMonthlyInterestBase()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void save_shouldSaveNewCheckingAccount() {
        // Given
        CheckingAccount account = buildCheckingAccount(BigDecimal.TEN).toBuilder()
                .id(null).createdAt(null).updatedAt(null)
                .build();

        // When
        CheckingAccount savedAccount = (CheckingAccount) accountRepository.save(account);

        // Then
        assertThat(savedAccount.getId()).isNotNull();
        assertThat(savedAccount.getCreatedAt()).isNotNull();
        assertThat(savedAccount.getUpdatedAt()).isNotNull();
        assertThat(savedAccount.getOwnerName()).isEqualTo("John Doe");
        assertThat(savedAccount.getBalance()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void save_shouldUpdateSavingsAccount() {
        // Given
        SavingsAccount account = buildSavingsAccount(BigDecimal.TEN);
        Account savedAccount = accountRepository.save(account);
        Instant originalUpdatedAt = savedAccount.getUpdatedAt();

        // When
        savedAccount.setBalance(BigDecimal.valueOf(2000));
        Account updatedAccount = accountRepository.save(savedAccount);

        // Then
        assertThat(updatedAccount.getId()).isEqualTo(savedAccount.getId());
        assertThat(updatedAccount.getCreatedAt()).isEqualTo(savedAccount.getCreatedAt());
        assertThat(updatedAccount.getUpdatedAt()).isAfter(originalUpdatedAt);
        assertThat(updatedAccount.getBalance()).isEqualTo(BigDecimal.valueOf(2000));
    }

    @Test
    void findById_shouldFindAccount() {
        // Given
        Account account = buildCheckingAccount(BigDecimal.TEN);
        Account savedAccount = accountRepository.save(account);

        // When
        Optional<Account> retrievedAccount = accountRepository.findById(savedAccount.getId());

        // Then
        assertThat(retrievedAccount).isPresent();
        assertThat(retrievedAccount.get().getId()).isEqualTo(savedAccount.getId());
        assertThat(retrievedAccount.get().getOwnerName()).isEqualTo("John Doe");
        assertThat(retrievedAccount.get().getBalance()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenAccountDoesNotExist() {
        // Given
        long nonExistentId = 999;

        // When
        Optional<Account> retrievedAccount = accountRepository.findById(nonExistentId);

        // Then
        assertThat(retrievedAccount).isEmpty();
    }
}
