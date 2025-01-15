package com.technicaltest.bankingapp.config;

import com.technicaltest.bankingapp.exception.ApplicationException;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Properties;

@UtilityClass
public class ConfigLoader {

    private static final String CONFIG_FILE = "application.properties";
    public static final Config CONFIGURATION = loadConfig();

    public static Config loadConfig() {
        var properties = new Properties();
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            properties.load(input);

            // Parse values from the properties file
            BigDecimal savingsInterestRate = new BigDecimal(properties.getProperty("savings.account.interest-rate"));
            BigDecimal savingsWithdrawMonthlyLimit = new BigDecimal(properties.getProperty("savings.account.withdraw-monthly-limit"));

            return new Config(savingsInterestRate, savingsWithdrawMonthlyLimit);
        } catch (IOException | NullPointerException e) {
            throw new ApplicationException("Failed to load configuration", e);
        }
    }
}