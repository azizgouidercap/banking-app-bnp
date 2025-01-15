package com.technicaltest.bankingapp.service;

import com.technicaltest.bankingapp.dto.AccountDTO;
import com.technicaltest.bankingapp.enumeration.AccountType;
import com.technicaltest.bankingapp.exception.InvalidOperationException;
import com.technicaltest.bankingapp.exception.ResourceNotFoundException;
import com.technicaltest.bankingapp.mapper.AccountMapper;
import com.technicaltest.bankingapp.model.Account;
import com.technicaltest.bankingapp.model.CheckingAccount;
import com.technicaltest.bankingapp.model.SavingsAccount;
import com.technicaltest.bankingapp.repository.AccountRepository;

import java.math.BigDecimal;

import static com.technicaltest.bankingapp.config.ConfigLoader.CONFIGURATION;
import static com.technicaltest.bankingapp.utils.BigDecimalUtils.normalize;

public class AccountService {

    private final AccountRepository accountRepository;
    private final CalculationService calculationService;

    public AccountService() {
        this.accountRepository = new AccountRepository();
        this.calculationService = new CalculationService();
    }

    public AccountService(AccountRepository accountRepository, CalculationService calculationService) {
        this.accountRepository = accountRepository;
        this.calculationService = calculationService;
    }

    public AccountDTO createAccount(String ownerName, BigDecimal balance, int accountType) {
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
        return AccountMapper.toDTO(createdAccount);
    }

    public void depositMoney(long accountId, BigDecimal amount) {
        Account account = findById(accountId);
        account.setBalance(calculationService.addAmount(account.getBalance(), amount));
        if (AccountType.SAVINGS.equals(account.getAccountType())) {
            SavingsAccount savingsAccount = (SavingsAccount) account;
            savingsAccount.setMonthlyInterestBase(savingsAccount.getBalance().max(savingsAccount.getMonthlyInterestBase()));
        }
        accountRepository.save(account);
    }

    public void withdrawMoney(long accountId, BigDecimal amount) {
        Account account = findById(accountId);
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
        accountRepository.save(account);
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
        return findById(accountId).getBalance();
    }

    public BigDecimal calculateInterest(Long accountId) {
        Account account = findById(accountId);
        if (!AccountType.SAVINGS.equals(account.getAccountType())) {
            throw new InvalidOperationException("Interest calculation is only applicable to savings accounts.");
        }

        SavingsAccount savingsAccount = (SavingsAccount) account;

        BigDecimal interest = calculationService.calculateSavingsInterest(savingsAccount.getMonthlyInterestBase());
        BigDecimal newBalance = calculationService.addAmount(savingsAccount.getBalance(), interest);
        savingsAccount.setBalance(newBalance);
        savingsAccount.setMonthlyInterestBase(newBalance);
        accountRepository.save(savingsAccount);
        return interest;
    }

    private Account findById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
    }

}
