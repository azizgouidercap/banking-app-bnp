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

    @Mock
    private CalculationService calculationService;

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
    void depositMoney_shouldSucceed_whenAccountTypeIsChecking() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(500);
        Account mockAccount = buildCheckingAccount(BigDecimal.valueOf(1000));
        when(accountRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(mockAccount));
        when(calculationService.addAmount(any(), any())).thenReturn(BigDecimal.valueOf(1500));

        // When
        accountService.depositMoney(DEFAULT_ID, amount);

        // Then
        assertThat(mockAccount.getBalance()).isEqualTo(BigDecimal.valueOf(1500));
        ArgumentCaptor<CheckingAccount> checkingAccountArgumentCaptor = ArgumentCaptor.forClass(CheckingAccount.class);
        verify(accountRepository, times(1)).save(checkingAccountArgumentCaptor.capture());
        CheckingAccount checkingAccount = checkingAccountArgumentCaptor.getValue();
        assertThat(checkingAccount.getBalance()).isEqualTo(BigDecimal.valueOf(1500));
        verify(calculationService).addAmount(any(), any());
    }

    @Test
    void depositMoney_shouldSucceed_whenAccountTypeIsSavings() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(500);
        Account mockAccount = buildSavingsAccount(BigDecimal.valueOf(1000), BigDecimal.valueOf(2000));
        when(accountRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(mockAccount));
        when(calculationService.addAmount(any(), any())).thenReturn(BigDecimal.valueOf(1500));

        // When
        accountService.depositMoney(DEFAULT_ID, amount);

        // Then
        assertThat(mockAccount.getBalance()).isEqualTo(BigDecimal.valueOf(1500));
        ArgumentCaptor<SavingsAccount> savingsAccountArgumentCaptor = ArgumentCaptor.forClass(SavingsAccount.class);
        verify(accountRepository, times(1)).save(savingsAccountArgumentCaptor.capture());
        SavingsAccount savingsAccount = savingsAccountArgumentCaptor.getValue();
        assertThat(savingsAccount.getBalance()).isEqualTo(BigDecimal.valueOf(1500));
        assertThat(savingsAccount.getMonthlyInterestBase()).isEqualTo(BigDecimal.valueOf(2000));
        verify(calculationService).addAmount(any(), any());
    }

    @Test
    void depositMoney_shouldUpdateMonthInterestBase_whenOldValueIsLessThanCurrentValue() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(500);
        Account mockAccount = buildSavingsAccount(BigDecimal.valueOf(1000), BigDecimal.valueOf(1000));
        when(accountRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(mockAccount));
        when(calculationService.addAmount(any(), any())).thenReturn(BigDecimal.valueOf(1500));

        // When
        accountService.depositMoney(DEFAULT_ID, amount);

        // Then
        assertThat(mockAccount.getBalance()).isEqualTo(BigDecimal.valueOf(1500));
        ArgumentCaptor<SavingsAccount> savingsAccountArgumentCaptor = ArgumentCaptor.forClass(SavingsAccount.class);
        verify(accountRepository, times(1)).save(savingsAccountArgumentCaptor.capture());
        SavingsAccount savingsAccount = savingsAccountArgumentCaptor.getValue();
        assertThat(savingsAccount.getBalance()).isEqualTo(BigDecimal.valueOf(1500));
        assertThat(savingsAccount.getMonthlyInterestBase()).isEqualTo(BigDecimal.valueOf(1500));
        verify(calculationService).addAmount(any(), any());
    }

    @Test
    void depositMoney_shouldThrowException_whenAccountIsNotFound() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(500);
        when(accountRepository.findById(DEFAULT_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> accountService.depositMoney(DEFAULT_ID, amount))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Account with ID 1 not found.");
        verify(calculationService, never()).addAmount(any(), any());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void withdrawMoney_shouldSucceed_whenAccountTypeIsChecking() {
        // Given
        long accountId = 1L;
        BigDecimal amount = BigDecimal.valueOf(200);
        Account mockAccount = buildCheckingAccount(BigDecimal.valueOf(1000));
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(mockAccount));
        when(calculationService.subtractAmount(any(), any())).thenReturn(BigDecimal.valueOf(800));

        // Act
        accountService.withdrawMoney(accountId, amount);

        // Assert
        assertThat(mockAccount.getBalance()).isEqualTo(BigDecimal.valueOf(800));
        verify(accountRepository, times(1)).save(mockAccount);
    }

    @Test
    void withdrawMoney_InsufficientBalance_ThrowsException() {
        // Arrange
        long accountId = 1L;
        BigDecimal amount = BigDecimal.valueOf(1500);
        Account mockAccount = buildCheckingAccount(BigDecimal.valueOf(1000));
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(mockAccount));

        // Act & Assert
        assertThatThrownBy(() -> accountService.withdrawMoney(accountId, amount))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessage("Insufficient balance for withdrawal.");
        verify(calculationService, never()).subtractAmount(any(), any());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void calculateInterest_SavingsAccount_Success() {
        // Arrange
        long accountId = 1L;
        BigDecimal monthlyInterestBase = BigDecimal.valueOf(1000);
        BigDecimal interest = BigDecimal.valueOf(50);
        SavingsAccount mockAccount = buildSavingsAccount(BigDecimal.valueOf(1000), monthlyInterestBase);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(mockAccount));
        when(calculationService.calculateSavingsInterest(monthlyInterestBase)).thenReturn(interest);
        when(calculationService.addAmount(any(), any())).thenReturn(BigDecimal.valueOf(1050));

        // Act
        BigDecimal result = accountService.calculateInterest(accountId);

        // Assert
        assertThat(result).isEqualTo(interest);
        assertThat(mockAccount.getBalance()).isEqualTo(BigDecimal.valueOf(1050));
        verify(accountRepository, times(1)).save(mockAccount);
    }

    @Test
    void calculateInterest_InvalidAccountType_ThrowsException() {
        // Arrange
        long accountId = 1L;
        Account mockAccount = buildCheckingAccount(BigDecimal.valueOf(1000));
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(mockAccount));

        // Act & Assert
        assertThatThrownBy(() -> accountService.calculateInterest(accountId))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessage("Interest calculation is only applicable to savings accounts.");
        verify(calculationService, never()).calculateSavingsInterest(any());
        verify(accountRepository, never()).save(any());
    }
}
