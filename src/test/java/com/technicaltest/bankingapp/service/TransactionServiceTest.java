package com.technicaltest.bankingapp.service;

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
import java.util.Optional;

import static com.technicaltest.bankingapp.builder.AccountBuilderFactory.*;
import static com.technicaltest.bankingapp.builder.AccountBuilderFactory.buildCheckingAccount;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private AccountService accountService;

    @Mock
    private CalculationService calculationService;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void depositMoney_shouldSucceed_whenAccountTypeIsChecking() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(500);
        Account mockAccount = buildCheckingAccount(BigDecimal.valueOf(1000));
        when(accountService.findById(DEFAULT_ID)).thenReturn(mockAccount);
        when(calculationService.addAmount(any(), any())).thenReturn(BigDecimal.valueOf(1500));

        // When
        transactionService.depositMoney(DEFAULT_ID, amount);

        // Then
        assertThat(mockAccount.getBalance()).isEqualTo(BigDecimal.valueOf(1500));
        ArgumentCaptor<CheckingAccount> checkingAccountArgumentCaptor = ArgumentCaptor.forClass(CheckingAccount.class);
        verify(accountService, times(1)).save(checkingAccountArgumentCaptor.capture());
        CheckingAccount checkingAccount = checkingAccountArgumentCaptor.getValue();
        assertThat(checkingAccount.getBalance()).isEqualTo(BigDecimal.valueOf(1500));
        verify(calculationService).addAmount(any(), any());
    }

    @Test
    void depositMoney_shouldSucceed_whenAccountTypeIsSavings() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(500);
        Account mockAccount = buildSavingsAccount(BigDecimal.valueOf(1000), BigDecimal.valueOf(2000));
        when(accountService.findById(DEFAULT_ID)).thenReturn(mockAccount);
        when(calculationService.addAmount(any(), any())).thenReturn(BigDecimal.valueOf(1500));

        // When
        transactionService.depositMoney(DEFAULT_ID, amount);

        // Then
        assertThat(mockAccount.getBalance()).isEqualTo(BigDecimal.valueOf(1500));
        ArgumentCaptor<SavingsAccount> savingsAccountArgumentCaptor = ArgumentCaptor.forClass(SavingsAccount.class);
        verify(accountService, times(1)).save(savingsAccountArgumentCaptor.capture());
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
        when(accountService.findById(DEFAULT_ID)).thenReturn(mockAccount);
        when(calculationService.addAmount(any(), any())).thenReturn(BigDecimal.valueOf(1500));

        // When
        transactionService.depositMoney(DEFAULT_ID, amount);

        // Then
        assertThat(mockAccount.getBalance()).isEqualTo(BigDecimal.valueOf(1500));
        ArgumentCaptor<SavingsAccount> savingsAccountArgumentCaptor = ArgumentCaptor.forClass(SavingsAccount.class);
        verify(accountService, times(1)).save(savingsAccountArgumentCaptor.capture());
        SavingsAccount savingsAccount = savingsAccountArgumentCaptor.getValue();
        assertThat(savingsAccount.getBalance()).isEqualTo(BigDecimal.valueOf(1500));
        assertThat(savingsAccount.getMonthlyInterestBase()).isEqualTo(BigDecimal.valueOf(1500));
        verify(calculationService).addAmount(any(), any());
    }

    @Test
    void depositMoney_shouldThrowException_whenAccountIsNotFound() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(500);
        when(accountService.findById(DEFAULT_ID)).thenThrow(new ResourceNotFoundException("Account", DEFAULT_ID));

        // When & Then
        assertThatThrownBy(() -> transactionService.depositMoney(DEFAULT_ID, amount))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Account with ID 1 not found.");
        verify(calculationService, never()).addAmount(any(), any());
        verify(accountService, never()).save(any());
    }

    @Test
    void withdrawMoney_shouldSucceed_whenAccountTypeIsChecking() {
        // Given
        long accountId = 1L;
        BigDecimal amount = BigDecimal.valueOf(200);
        Account mockAccount = buildCheckingAccount(BigDecimal.valueOf(1000));
        when(accountService.findById(accountId)).thenReturn(mockAccount);
        when(calculationService.subtractAmount(any(), any())).thenReturn(BigDecimal.valueOf(800));

        // Act
        transactionService.withdrawMoney(accountId, amount);

        // Assert
        assertThat(mockAccount.getBalance()).isEqualTo(BigDecimal.valueOf(800));
        verify(accountService, times(1)).save(mockAccount);
    }

    @Test
    void withdrawMoney_InsufficientBalance_ThrowsException() {
        // Arrange
        long accountId = 1L;
        BigDecimal amount = BigDecimal.valueOf(1500);
        Account mockAccount = buildCheckingAccount(BigDecimal.valueOf(1000));
        when(accountService.findById(accountId)).thenReturn(mockAccount);

        // Act & Assert
        assertThatThrownBy(() -> transactionService.withdrawMoney(accountId, amount))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessage("Insufficient balance for withdrawal.");
        verify(calculationService, never()).subtractAmount(any(), any());
        verify(accountService, never()).save(any());
    }

    @Test
    void withdrawMoney_shouldThrowException_whenAccountIsNotFound() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(500);
        when(accountService.findById(DEFAULT_ID)).thenThrow(new ResourceNotFoundException("Account", DEFAULT_ID));

        // When & Then
        assertThatThrownBy(() -> transactionService.withdrawMoney(DEFAULT_ID, amount))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Account with ID 1 not found.");
        verify(calculationService, never()).subtractAmount(any(), any());
        verify(accountService, never()).save(any());
    }

    @Test
    void calculateInterest_SavingsAccount_Success() {
        // Arrange
        long accountId = 1L;
        BigDecimal monthlyInterestBase = BigDecimal.valueOf(1000);
        BigDecimal interest = BigDecimal.valueOf(50);
        SavingsAccount mockAccount = buildSavingsAccount(BigDecimal.valueOf(1000), monthlyInterestBase);
        when(accountService.findById(accountId)).thenReturn(mockAccount);
        when(calculationService.calculateSavingsInterest(monthlyInterestBase)).thenReturn(interest);
        when(calculationService.addAmount(any(), any())).thenReturn(BigDecimal.valueOf(1050));

        // Act
        BigDecimal result = transactionService.calculateInterest(accountId);

        // Assert
        assertThat(result).isEqualTo(interest);
        assertThat(mockAccount.getBalance()).isEqualTo(BigDecimal.valueOf(1050));
        verify(accountService, times(1)).save(mockAccount);
    }

    @Test
    void calculateInterest_InvalidAccountType_ThrowsException() {
        // Arrange
        long accountId = 1L;
        Account mockAccount = buildCheckingAccount(BigDecimal.valueOf(1000));
        when(accountService.findById(accountId)).thenReturn(mockAccount);

        // Act & Assert
        assertThatThrownBy(() -> transactionService.calculateInterest(accountId))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessage("Interest calculation is only applicable to savings accounts.");
        verify(calculationService, never()).calculateSavingsInterest(any());
        verify(accountService, never()).save(any());
    }

    @Test
    void calculateInterest_shouldThrowException_whenAccountIsNotFound() {
        // Given
        when(accountService.findById(DEFAULT_ID)).thenThrow(new ResourceNotFoundException("Account", DEFAULT_ID));

        // When & Then
        assertThatThrownBy(() -> transactionService.calculateInterest(DEFAULT_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Account with ID 1 not found.");
        verify(calculationService, never()).calculateSavingsInterest(any());
        verify(accountService, never()).save(any());
    }
}
