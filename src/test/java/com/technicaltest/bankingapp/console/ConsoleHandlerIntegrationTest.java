package com.technicaltest.bankingapp.console;

import com.technicaltest.bankingapp.database.InMemoryDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

class ConsoleHandlerIntegrationTest {

    @BeforeEach
    void setUp() {
        InMemoryDatabase.idCounter.clear();
        InMemoryDatabase.database.clear();
    }

    @Test
    void start_shouldNotAcceptCommand_whenItIsNotANumber() {
        // Given
        String simulatedInput = """
                X
                6
                """;
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        System.setIn(inputStream);
        System.setOut(printStream);

        ConsoleHandler consoleHandler = new ConsoleHandler();

        // When
        consoleHandler.start();

        // Then
        String output = outputStream.toString();

        assertThat(output)
                .contains("Invalid choice. Please try again.")
                .contains("Goodbye!");
    }

    @Test
    void start_shouldNotAcceptCommand_whenItIsANumber() {
        // Given
        String simulatedInput = """
                9
                6
                """;
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        System.setIn(inputStream);
        System.setOut(printStream);

        ConsoleHandler consoleHandler = new ConsoleHandler();

        // When
        consoleHandler.start();

        // Then
        String output = outputStream.toString();

        assertThat(output)
                .contains("Invalid choice. Please try again.")
                .contains("Goodbye!");
    }

    @Test
    void start_shouldCreateAccount() {
        // Given
        String simulatedInput = """
                1
                John Doe
                1000
                1
                6
                """;
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        System.setIn(inputStream);
        System.setOut(printStream);

        ConsoleHandler consoleHandler = new ConsoleHandler();

        // When
        consoleHandler.start();

        // Then
        String output = outputStream.toString();

        assertThat(output)
                .contains("Enter your choice:")
                .contains("Enter your name:")
                .contains("Enter initial balance:")
                .contains("Choose account type (1 for checking, 2 for savings):")
                .contains("created successfully")
                .contains("Goodbye!");
    }

    @Test
    void start_shouldDepositAmount() {
        // Given
        String simulatedInput = """
                 1
                John Doe
                1000
                1
                2
                1
                500
                4
                1
                6
                """;
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        System.setIn(inputStream);
        System.setOut(printStream);

        ConsoleHandler consoleHandler = new ConsoleHandler();

        // When
        consoleHandler.start();

        // Then
        String output = outputStream.toString();

        assertThat(output)
                .contains("Enter account ID:")
                .contains("Enter amount to deposit:")
                .contains("deposited successfully")
                .contains("Account balance: 1500.00")
                .contains("Goodbye!");
    }
}
