package com.technicaltest.bankingapp.service;

import com.technicaltest.bankingapp.dto.AccountDTO;
import com.technicaltest.bankingapp.enumeration.AccountType;
import com.technicaltest.bankingapp.exception.InvalidOperationException;
import com.technicaltest.bankingapp.exception.ResourceNotFoundException;
import com.technicaltest.bankingapp.model.Account;
import com.technicaltest.bankingapp.model.CheckingAccount;
import com.technicaltest.bankingapp.model.SavingsAccount;
import com.technicaltest.bankingapp.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.technicaltest.bankingapp.builder.AccountBuilderFactory.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.math.RoundingMode;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void createAccount_shouldCreateCheckingAccount() {
        // Given
        BigDecimal balance = BigDecimal.valueOf(1000);
        int accountType = 1;
        Account mockAccount = buildCheckingAccount(balance);
        when(accountRepository.save(any(Account.class))).thenReturn(mockAccount);

        // When
        AccountDTO result = accountService.createAccount(OWNER_NAME, balance, accountType);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getOwnerName()).isEqualTo(OWNER_NAME);
        assertThat(result.getBalance()).isEqualTo(balance);
        assertThat(result.getAccountType()).isEqualTo(AccountType.CHECKING);
        assertThat(result.getId()).isEqualTo(DEFAULT_ID);
        verify(accountRepository, never()).save(any(SavingsAccount.class));
        ArgumentCaptor<CheckingAccount> checkingAccountArgumentCaptor = ArgumentCaptor.forClass(CheckingAccount.class);
        verify(accountRepository).save(checkingAccountArgumentCaptor.capture());
        CheckingAccount checkingAccount = checkingAccountArgumentCaptor.getValue();
        assertThat(checkingAccount.getBalance()).isEqualTo(balance.setScale(2, RoundingMode.HALF_UP));
        assertThat(checkingAccount.getAccountType()).isEqualTo(AccountType.CHECKING);
        assertThat(checkingAccount.getId()).isNull();
        assertThat(checkingAccount.getOwnerName()).isEqualTo(OWNER_NAME);
    }

    @Test
    void createAccount_shouldCreateSavingsAccount() {
        // Given
        BigDecimal balance = BigDecimal.valueOf(1000);
        int accountType = 2;
        Account mockAccount = buildSavingsAccount(balance);
        when(accountRepository.save(any(Account.class))).thenReturn(mockAccount);

        // When
        AccountDTO result = accountService.createAccount(OWNER_NAME, balance, accountType);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getOwnerName()).isEqualTo(OWNER_NAME);
        assertThat(result.getBalance()).isEqualTo(balance);
        assertThat(result.getAccountType()).isEqualTo(AccountType.SAVINGS);
        assertThat(result.getId()).isEqualTo(DEFAULT_ID);
        verify(accountRepository, never()).save(any(CheckingAccount.class));
        ArgumentCaptor<SavingsAccount> savingsAccountArgumentCaptor = ArgumentCaptor.forClass(SavingsAccount.class);
        verify(accountRepository).save(savingsAccountArgumentCaptor.capture());
        SavingsAccount savingsAccount = savingsAccountArgumentCaptor.getValue();
        assertThat(savingsAccount.getBalance()).isEqualTo(balance.setScale(2, RoundingMode.HALF_EVEN));
        assertThat(savingsAccount.getAccountType()).isEqualTo(AccountType.SAVINGS);
        assertThat(savingsAccount.getId()).isNull();
        assertThat(savingsAccount.getMonthlyInterestBase()).isEqualTo(balance.setScale(2, RoundingMode.HALF_EVEN));
        assertThat(savingsAccount.getOwnerName()).isEqualTo(OWNER_NAME);

    }

    @Test
    void createAccount_shouldThrowException_whenAccountTypeIsInvalid() {
        // Given
        BigDecimal balance = BigDecimal.valueOf(1000);
        int accountType = 3;

        // When & Then
        assertThatThrownBy(() -> accountService.createAccount(OWNER_NAME, balance, accountType))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessage("Invalid account type.");
        verify(accountRepository, never()).save(any());
    }

    @Test
    void findById_shouldThrowException_whenAccountIsNotFound() {
        // Given
        when(accountRepository.findById(DEFAULT_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> accountService.findById(DEFAULT_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Account with ID 1 not found.");
        verify(accountRepository).findById(any());
    }

    @Test
    void findById_shouldReturnAccount() {
        // Given
        Account mockAccount = buildSavingsAccount(BigDecimal.valueOf(1000));
        when(accountRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(mockAccount));

        // When
        Account result = accountService.findById(DEFAULT_ID);

        // Then
        assertThat(result).isEqualTo(mockAccount);
        verify(accountRepository).findById(any());
    }

    @Test
    void save_shouldSucceed() {
        // Given
        Account account = buildSavingsAccount(BigDecimal.valueOf(1000));
        Account mockAccount = buildSavingsAccount(BigDecimal.valueOf(1000));
        when(accountRepository.save(any())).thenReturn(mockAccount);

        // When
        Account result = accountService.save(account);

        // Then
        assertThat(result).isEqualTo(mockAccount);
        verify(accountRepository).save(any());
    }
}
