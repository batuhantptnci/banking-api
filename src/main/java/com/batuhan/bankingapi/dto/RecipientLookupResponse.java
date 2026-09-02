package com.batuhan.bankingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecipientLookupResponse {

    private String accountNumber;
    private String fullName;
}