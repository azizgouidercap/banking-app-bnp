package com.technicaltest.bankingapp;

import com.technicaltest.bankingapp.console.ConsoleHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BankingApplication {
    public static void main(String[] args) {
        log.info("Banking Application Started successfully !");
        new ConsoleHandler().start();
        log.info("Banking Application Shutdown Successfully !");
    }
}