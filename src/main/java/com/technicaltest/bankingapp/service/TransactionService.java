package com.technicaltest.bankingapp.service;

import com.technicaltest.bankingapp.enumeration.AccountType;
import com.technicaltest.bankingapp.exception.InvalidOperationException;
import com.technicaltest.bankingapp.model.Account;
import com.technicaltest.bankingapp.model.SavingsAccount;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

import static com.technicaltest.bankingapp.config.ConfigLoader.CONFIGURATION;

@Slf4j
public class TransactionService {

    private final AccountService accountService;
    private final CalculationService calculationService;

    public TransactionService() {
        this.accountService = new AccountService();
        this.calculationService = new CalculationService();
    }

    public TransactionService(AccountService accountService, CalculationService calculationService) {
        this.accountService = accountService;
        this.calculationService = calculationService;
    }

    public void depositMoney(long accountId, BigDecimal amount) {
        log.debug("AccountService - Initiating deposit.");
        Account account = accountService.findById(accountId);
        account.setBalance(calculationService.addAmount(account.getBalance(), amount));
        if (AccountType.SAVINGS.equals(account.getAccountType())) {
            SavingsAccount savingsAccount = (SavingsAccount) account;
            savingsAccount.setMonthlyInterestBase(savingsAccount.getBalance().max(savingsAccount.getMonthlyInterestBase()));
        }
        accountService.save(account);
        log.debug("AccountService - Account deposit successfully.");
    }

    public void withdrawMoney(long accountId, BigDecimal amount) {
        log.debug("AccountService - Initiating withdraw.");
        Account account = accountService.findById(accountId);
        switch (account.getAccountType()) {
            case CHECKING -> {
                validateCheckingAccountWithdrawal(account, amount);
                performWithdrawal(account, amount);
            }
            case SAVINGS -> {
                validateSavingsAccountWithdrawal(amount);
                performWithdrawal(account, amount);
            }
            default -> throw new InvalidOperationException("Withdrawal is not supported for this type of account.");
        }
        accountService.save(account);
        log.debug("AccountService - Account withdraw successfully.");
    }

    private void validateCheckingAccountWithdrawal(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InvalidOperationException("Insufficient balance for withdrawal.");
        }
    }

    private void validateSavingsAccountWithdrawal(BigDecimal amount) {
        if (amount.compareTo(CONFIGURATION.savingsWithdrawMonthlyLimit()) > 0) {
            throw new InvalidOperationException("Withdrawal amount exceeds the maximum allowed limit of " + CONFIGURATION.savingsWithdrawMonthlyLimit().toString() + " for savings accounts.");
        }
    }

    private void performWithdrawal(Account account, BigDecimal amount) {
        account.setBalance(calculationService.subtractAmount(account.getBalance(), amount));
    }

    public BigDecimal getBalance(long accountId) {
        return accountService.findById(accountId).getBalance();
    }

    public BigDecimal calculateInterest(Long accountId) {
        log.debug("AccountService - Initiating calculate interest.");
        Account account = accountService.findById(accountId);
        if (!AccountType.SAVINGS.equals(account.getAccountType())) {
            throw new InvalidOperationException("Interest calculation is only applicable to savings accounts.");
        }

        SavingsAccount savingsAccount = (SavingsAccount) account;

        BigDecimal interest = calculationService.calculateSavingsInterest(savingsAccount.getMonthlyInterestBase());
        BigDecimal newBalance = calculationService.addAmount(savingsAccount.getBalance(), interest);
        savingsAccount.setBalance(newBalance);
        savingsAccount.setMonthlyInterestBase(newBalance);
        accountService.save(savingsAccount);
        log.debug("AccountService - Account calculate interest successfully.");
        return interest;
    }
}
