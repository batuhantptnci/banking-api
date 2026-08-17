package com.batuhan.bankingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class AccountResponse {

    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private Long userId;
}