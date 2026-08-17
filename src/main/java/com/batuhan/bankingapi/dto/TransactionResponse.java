package com.batuhan.bankingapi.dto;

import com.batuhan.bankingapi.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TransactionResponse {

    private Long id;
    private TransactionType type;
    private BigDecimal amount;
    private Long accountId;
    private Long targetAccountId;
    private LocalDateTime createdAt;
}