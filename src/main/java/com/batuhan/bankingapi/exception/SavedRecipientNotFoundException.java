package com.batuhan.bankingapi.exception;

public class SavedRecipientNotFoundException
        extends RuntimeException {

    public SavedRecipientNotFoundException(
            String message
    ) {
        super(message);
    }
}