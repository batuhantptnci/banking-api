package com.batuhan.bankingapi.exception;

public class SavedRecipientAlreadyExistsException
        extends RuntimeException {

    public SavedRecipientAlreadyExistsException(
            String message
    ) {
        super(message);
    }
}