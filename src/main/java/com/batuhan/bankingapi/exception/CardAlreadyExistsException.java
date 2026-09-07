package com.batuhan.bankingapi.exception;

public class CardAlreadyExistsException
        extends RuntimeException {

    public CardAlreadyExistsException(
            String message
    ) {
        super(message);
    }
}