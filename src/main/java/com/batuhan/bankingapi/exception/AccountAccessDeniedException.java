package com.batuhan.bankingapi.exception;

public class AccountAccessDeniedException extends RuntimeException {

    public AccountAccessDeniedException(String message) {
        super(message);
    }
}