package com.technicaltest.bankingapp.mapper;

import com.technicaltest.bankingapp.dto.AccountDTO;
import com.technicaltest.bankingapp.enumeration.AccountType;
import com.technicaltest.bankingapp.model.Account;
import com.technicaltest.bankingapp.model.CheckingAccount;
import com.technicaltest.bankingapp.model.SavingsAccount;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.technicaltest.bankingapp.builder.AccountBuilderFactory.buildCheckingAccount;
import static com.technicaltest.bankingapp.builder.AccountBuilderFactory.buildSavingsAccount;
import static org.assertj.core.api.Assertions.assertThat;

class AccountMapperTest {


    @Test
    public void toDto_shouldReturnNull_WhenAccountIsNull() {
        // Given
        Account account = null;

        // When
        AccountDTO accountDTO = AccountMapper.toDTO(account);

        // Then
        assertThat(accountDTO).isNull();
    }

    @Test
    public void toDto_shouldSavingsAccountMapToDto_WhenAccountIsNotNull() {
        // Given
        SavingsAccount savingsAccount = buildSavingsAccount(BigDecimal.TEN, BigDecimal.valueOf(10.12));

        // When
        AccountDTO accountDTO = AccountMapper.toDTO(savingsAccount);

        // Then
        assertThat(accountDTO).isNotNull();
        assertThat(accountDTO.getId()).isEqualTo(1L);
        assertThat(accountDTO.getAccountType()).isEqualTo(AccountType.SAVINGS);
        assertThat(accountDTO.getBalance()).isEqualTo(BigDecimal.TEN);
        assertThat(accountDTO.getOwnerName()).isEqualTo("John Doe");
    }

    @Test
    public void toDto_shouldMapCheckingAccountToDto_WhenAccountIsNotNull() {
        // Given
        CheckingAccount checkingAccount = buildCheckingAccount(BigDecimal.TEN);

        // When
        AccountDTO accountDTO = AccountMapper.toDTO(checkingAccount);

        // Then
        assertThat(accountDTO).isNotNull();
        assertThat(accountDTO.getId()).isEqualTo(1L);
        assertThat(accountDTO.getAccountType()).isEqualTo(AccountType.CHECKING);
        assertThat(accountDTO.getBalance()).isEqualTo(BigDecimal.TEN);
        assertThat(accountDTO.getOwnerName()).isEqualTo("John Doe");
    }
}
