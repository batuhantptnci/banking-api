package com.batuhan.bankingapi.exception;

public class InvalidCardOperationException
        extends RuntimeException {

    public InvalidCardOperationException(
            String message
    ) {
        super(message);
    }
}