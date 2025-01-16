package com.technicaltest.bankingapp.console;

import com.technicaltest.bankingapp.dto.AccountDTO;
import com.technicaltest.bankingapp.enumeration.AccountType;
import com.technicaltest.bankingapp.exception.BusinessException;
import com.technicaltest.bankingapp.service.AccountService;
import com.technicaltest.bankingapp.service.TransactionService;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Scanner;

import static com.technicaltest.bankingapp.utils.BigDecimalUtils.normalize;
import static com.technicaltest.bankingapp.utils.ValidationUtils.requireNonNull;
import static com.technicaltest.bankingapp.utils.ValidationUtils.requirePositiveNumber;

@Slf4j
public class ConsoleHandler {

    private final AccountService accountService = new AccountService();
    private final TransactionService transactionService = new TransactionService();
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Entry point of the console-based application.
     * Continuously displays a menu, handles user input, and invokes corresponding operations
     * until the user chooses to quit.
     */
    public void start() {
        boolean running = true;

        while (running) {
            try {
                displayMenu();

                int choice = getValidatedUserChoice();
                running = handleUserChoice(choice);

            } catch (BusinessException e) {
                log.warn(e.getMessage());
                System.err.println("Error: " + e.getMessage());
            } catch (Exception e) {
                log.error(e.getMessage());
                System.err.println("An unexpected error occurred: " + e.getMessage());
            }
        }
        scanner.close();
    }

    /**
     * Validates and retrieves the user's menu choice.
     *
     * @return the user's menu choice as an integer
     */
    private int getValidatedUserChoice() {
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid choice. Please try again.");
            scanner.next();
            return -1;
        }
        int choice = scanner.nextInt();
        scanner.nextLine();
        return choice;
    }

    /**
     * Handles the user's menu choice by delegating to the appropriate method.
     *
     * @param choice the user's menu choice
     * @return false if the user chooses to quit, true otherwise
     */
    private boolean handleUserChoice(int choice) {
        switch (choice) {
            case 1 -> createAccount();
            case 2 -> depositMoney();
            case 3 -> withdrawMoney();
            case 4 -> displayBalance();
            case 5 -> calculateInterest();
            case 6 -> {
                System.out.println("Goodbye!");
                return false;
            }
            default -> System.out.println("Invalid choice. Please try again.");
        }
        return true;
    }

    /**
     * Displays the main menu with available options for the user.
     */
    private void displayMenu() {
        System.out.println("1. Create Account");
        System.out.println("2. Deposit Money");
        System.out.println("3. Withdraw Money");
        System.out.println("4. Display Balance");
        System.out.println("5. Calculate Interest (Savings Account)");
        System.out.println("6. Quit");
        System.out.print("Enter your choice: ");
    }

    /**
     * Handles the creation of a new account.
     * Prompts the user to enter their name and initial balance.
     */
    private void createAccount() {
        System.out.print("Enter your name: ");
        String ownerName = scanner.nextLine();
        requireNonNull(ownerName, "Owner Name");

        System.out.print("Enter initial balance: ");
        var balance = scanner.nextBigDecimal();
        requirePositiveNumber(balance, "Initial Balance");

        System.out.print("Choose account type (1 for checking, 2 for savings): ");
        int accountType = scanner.nextInt();
        requirePositiveNumber(accountType, "Account Type");

        AccountDTO createdAccount = accountService.createAccount(ownerName, balance, accountType);
        StringBuilder creationSuccessMessage = new StringBuilder();
        if (AccountType.SAVINGS.equals(createdAccount.getAccountType())) {
            creationSuccessMessage.append("Savings Account");
        } else if (AccountType.CHECKING.equals(createdAccount.getAccountType())) {
            creationSuccessMessage.append("Checking Account");
        }
        creationSuccessMessage.append(" created successfully. Account ID: ");
        creationSuccessMessage.append(createdAccount.getId());

        System.out.println(creationSuccessMessage);
    }

    /**
     * Handles depositing money into an account.
     * Prompts the user to enter the account ID and the deposit amount.
     */
    private void depositMoney() {
        System.out.print("Enter account ID: ");
        long accountId = scanner.nextLong();
        requirePositiveNumber(accountId, "Account ID");

        System.out.print("Enter amount to deposit: ");
        var amount = scanner.nextBigDecimal();
        requirePositiveNumber(amount, "Amount to Withdraw");

        transactionService.depositMoney(accountId, amount);
        System.out.println(amount + " deposited successfully.");
    }

    /**
     * Handles withdrawing money from an account.
     * Prompts the user to enter the account ID and the withdrawal amount.
     */
    private void withdrawMoney() {
        System.out.print("Enter account ID: ");
        long accountId = scanner.nextLong();
        System.out.print("Enter amount to withdraw: ");
        var amount = scanner.nextBigDecimal();

        transactionService.withdrawMoney(accountId, amount);
        System.out.println(amount + " withdrawn successfully.");
    }

    /**
     * Displays the current balance of an account.
     * Prompts the user to enter the account ID.
     */
    private void displayBalance() {
        System.out.print("Enter account ID: ");
        long accountId = scanner.nextLong();
        requirePositiveNumber(accountId, "Account ID");

        BigDecimal accountBalance = transactionService.getBalance(accountId);
        System.out.println("Account balance: " + accountBalance);
    }

    /**
     * Calculating and applying interests for balance of an account.
     * Prompts the user to enter the account ID.
     */
    private void calculateInterest() {
        System.out.print("Enter account ID: ");
        long accountId = scanner.nextLong();
        requirePositiveNumber(accountId, "Account ID");

        BigDecimal interest = transactionService.calculateInterest(accountId);
        System.out.printf("Interest for this month is %s euros%n", interest.toString());
    }
}